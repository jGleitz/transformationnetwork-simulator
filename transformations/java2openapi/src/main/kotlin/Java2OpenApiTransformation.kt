package de.joshuagleitze.transformationnetwork.transformations

import de.joshuagleitze.transformationnetwork.changemetamodel.AdditionChange
import de.joshuagleitze.transformationnetwork.changemetamodel.AttributeChange
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.ChangeSet
import de.joshuagleitze.transformationnetwork.changerecording.BaseModelTransformation
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecordingModel
import de.joshuagleitze.transformationnetwork.changerecording.ObservableModelTransformationType
import de.joshuagleitze.transformationnetwork.changerecording.createChecked
import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.metametamodel.byIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.ofType
import de.joshuagleitze.transformationnetwork.models.java.Class
import de.joshuagleitze.transformationnetwork.models.java.Interface
import de.joshuagleitze.transformationnetwork.models.java.JavaMetamodel
import de.joshuagleitze.transformationnetwork.models.java.Method
import de.joshuagleitze.transformationnetwork.models.openapi.Endpoint
import de.joshuagleitze.transformationnetwork.models.openapi.OpenApiMetamodel
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelCorrespondences.CorrespondenceTag

class Java2OpenApiTransformation(val javaModel: ChangeRecordingModel, val openApiModel: ChangeRecordingModel) :
    BaseModelTransformation() {
    override val type: ObservableModelTransformationType get() = Type
    override val leftModel: ChangeRecordingModel get() = javaModel
    override val rightModel: ChangeRecordingModel get() = openApiModel

    override fun isConsistent(): Boolean {
        return javaModel.objects.ofType(Interface.Metaclass).all { iface ->
            val implementation = correspondences.getLeftSecondCorrespondence(iface, IMPLEMENTATION)
            implementation != null && (iface.methods?.all { methodIdentifier ->
                val method = javaModel.requireObject(methodIdentifier)
                val endpoint = correspondences.getRightCorrespondence(method, METHOD_ENDPOINT)
                val classMethod = correspondences.getLeftSecondCorrespondence(method, METHOD_IMPLEMENTATION)
                endpoint != null
                    && endpoint.path == getEndpointPath(method)
                    && endpoint.method == getEndpointMethod(method)
                    && classMethod != null
                    && classMethod.name == method.name
                    && classMethod.parameters == method.parameters
                    && classMethod.visibility == method.visibility
                    && (classMethod.modifiers?.contains("override") ?: false)
            } ?: true)
        }
    }

    override fun processChangesChecked(leftSide: TransformationSide, rightSide: TransformationSide) {
        leftSide.propagateDeletionsToOtherSide(METHOD_ENDPOINT)
        leftSide.propagateDeletionsOnThisSide(IMPLEMENTATION, METHOD_IMPLEMENTATION)
        rightSide.propagateDeletionsToOtherSide(METHOD_ENDPOINT)
        rightSide.propagateDeletionsOnThisSide(IMPLEMENTATION, METHOD_IMPLEMENTATION)

        leftSide.additions.adding(Interface.Metaclass).forEach(::processJavaInterfaceAddition)
        leftSide.modifications.targetting(Interface.Metaclass).forEach(::processInterfaceModification)
        leftSide.modifications.targetting(Method.Metaclass).forEach(::processMethodModification)
    }

    private fun processJavaInterfaceAddition(addition: AdditionChange<Interface>) {
        val implementation = Class()
        implementation.implements = byIdentity(addition.addedObjectIdentity)
        javaModel += implementation
        correspondences.addLeftToLeftCorrespondence(
            addition.addedObjectIdentity,
            implementation.identity,
            IMPLEMENTATION
        )
    }

    private fun processInterfaceModification(modification: AttributeChange<Interface>) {
        val javaInterface = javaModel.requireObject(modification.targetObject)
        val implementation = correspondences.requireLeftSecondCorrespondence(javaInterface.identity, IMPLEMENTATION)
        with(Interface.Metaclass.Attributes) {
            when (modification.targetAttribute) {
                name -> implementation.name = javaInterface.name?.replace("Service", "Server")
                methods -> {// TODO deletion
                    javaInterface.methods?.forEach { interfaceMethodIdentifier ->
                        val interfaceMethod = javaModel.requireObject(interfaceMethodIdentifier)
                        val correspondingEndpoint =
                            correspondences.getRightCorrespondence(interfaceMethod, METHOD_ENDPOINT)
                        if (correspondingEndpoint == null) createEndpointForMethod(interfaceMethod)
                    }
                    implementation.methods = javaInterface.methods?.map { interfaceMethodIdentifier ->
                        val interfaceMethod = javaModel.requireObject(interfaceMethodIdentifier)
                        byIdentity(
                            (correspondences.getLeftSecondCorrespondence(interfaceMethod, METHOD_IMPLEMENTATION)
                                ?: createImplementationForMethod(interfaceMethod)).identity
                        )
                    }
                }
                else -> Unit
            }
        }
    }

    private fun createEndpointForMethod(javaMethod: Method): Endpoint {
        val endpoint = Endpoint().apply {
            method = getEndpointMethod(javaMethod)
            path = getEndpointPath(javaMethod)
        }
        openApiModel += endpoint
        correspondences.addLeftToRightCorrespondence(javaMethod, endpoint, METHOD_ENDPOINT)
        return endpoint
    }

    private fun createImplementationForMethod(interfaceMethod: Method): Method {
        val implementation = Method().apply {
            name = interfaceMethod.name
            parameters = interfaceMethod.parameters
            visibility = interfaceMethod.visibility
            modifiers = listOf("override")
        }
        javaModel += implementation
        correspondences.addLeftToLeftCorrespondence(interfaceMethod, implementation, METHOD_IMPLEMENTATION)
        return implementation
    }

    private fun processMethodModification(modification: AttributeChange<Method>) {
        val javaMethod = javaModel.requireObject(modification.targetObject)

        val endpoint = correspondences.getRightCorrespondence(javaMethod, METHOD_ENDPOINT)
        val implementation = correspondences.getLeftSecondCorrespondence(javaMethod.identity, METHOD_IMPLEMENTATION)

        with(Method.Metaclass.Attributes) {
            when (modification.targetAttribute) {
                name -> {
                    if (endpoint != null) endpoint.path = getEndpointPath(javaMethod)
                    if (implementation != null) implementation.name = javaMethod[name]
                }
                parameters -> {
                    if (endpoint != null) endpoint.method = getEndpointMethod(javaMethod)
                    if (implementation != null) implementation.parameters = javaMethod[parameters]
                }
                visiblity -> {
                    val nextChanges = openApiModel.recordChanges {
                        if (javaMethod[visiblity] != "public") {
                            if (endpoint != null) {
                                openApiModel -= endpoint
                                correspondences.removeCorrespondence(endpoint, METHOD_ENDPOINT)
                            }
                        }
                    }
                    processChanges(ChangeSet.EMPTY, nextChanges)
                    if (implementation != null) implementation.visibility = javaMethod[visiblity]
                }
            }
        }
    }

    private fun getEndpointPath(javaMethod: Method): String? {
        with(Method.Metaclass.Attributes) {
            val methodName = javaMethod[name]
            return if (methodName == null) null
            else {
                val prefixRemoved = methodName.removePrefix("get").removePrefix("set")
                val pathParts = prefixRemoved.toFirstLower().replace(Regex("([a-z])(A-Z)"), "$1$2")
                "/$pathParts"
            }
        }
    }

    private fun getEndpointMethod(javaMethod: Method): String? {
        with(Method.Metaclass.Attributes) {
            val parameters = javaMethod[parameters]
            return when {
                parameters == null -> null
                parameters.isEmpty() -> "GET"
                else -> "POST"
            }
        }
    }

    private fun String.toFirstLower(): String =
        if (this.isEmpty()) "" else this[0].toLowerCase() + this.substring(1)

    companion object Type : ObservableModelTransformationType {
        override val leftMetamodel get() = JavaMetamodel
        override val rightMetamodel get() = OpenApiMetamodel

        override fun create(leftModel: Model, rightModel: Model) =
            createChecked(leftModel, rightModel, ::Java2OpenApiTransformation)

        private val IMPLEMENTATION = CorrespondenceTag(Interface.Metaclass, Class.Metaclass)
        private val METHOD_IMPLEMENTATION = CorrespondenceTag(Method.Metaclass, Method.Metaclass)
        private val METHOD_ENDPOINT = CorrespondenceTag(Method.Metaclass, Endpoint.Metaclass)
    }
}
