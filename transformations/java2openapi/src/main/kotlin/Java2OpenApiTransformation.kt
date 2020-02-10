package de.joshuagleitze.transformationnetwork.transformations

import de.joshuagleitze.transformationnetwork.changemetamodel.AttributeChange
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.ChangeSet
import de.joshuagleitze.transformationnetwork.changerecording.BaseModelTransformation
import de.joshuagleitze.transformationnetwork.changerecording.BaseModelTransformationType
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecordingModel
import de.joshuagleitze.transformationnetwork.changerecording.ObservableModelTransformationType
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObject
import de.joshuagleitze.transformationnetwork.metametamodel.byIdentity
import de.joshuagleitze.transformationnetwork.models.java.Class
import de.joshuagleitze.transformationnetwork.models.java.Interface
import de.joshuagleitze.transformationnetwork.models.java.JavaMetamodel
import de.joshuagleitze.transformationnetwork.models.java.Method
import de.joshuagleitze.transformationnetwork.models.openapi.Endpoint
import de.joshuagleitze.transformationnetwork.models.openapi.OpenApiMetamodel
import de.joshuagleitze.transformationnetwork.transformations.Java2OpenApiTransformation.Tag
import de.joshuagleitze.transformationnetwork.transformations.Java2OpenApiTransformation.Tag.IMPLEMENTATION
import de.joshuagleitze.transformationnetwork.transformations.Java2OpenApiTransformation.Tag.INTERFACE_METHOD

class Java2OpenApiTransformation(val javaModel: ChangeRecordingModel, val openApiModel: ChangeRecordingModel) :
    BaseModelTransformation<Tag>() {
    override val type: ObservableModelTransformationType get() = Type
    override val leftModel: ChangeRecordingModel get() = javaModel
    override val rightModel: ChangeRecordingModel get() = openApiModel

    override fun processChangesChecked(leftSide: TransformationSide, rightSide: TransformationSide) {
        leftSide.propagateDeletionsToOtherSide()
        rightSide.propagateDeletionsToOtherSide()
        leftSide.processJavaAdditions()
        rightSide.processOpenApiAdditions()
        leftSide.modifications.executeFiltered(Interface.Metaclass, ::processInterfaceModification)
        leftSide.modifications.executeFiltered(Method.Metaclass, ::processMethodModification)
    }

    private fun TransformationSide.processJavaAdditions() {
        for (addition in additions) {
            when (addition.addedObjectClass) {
                Interface.Metaclass -> {
                    val implementation = Class()
                    javaModel += implementation
                    correspondences.addLeftToRightCorrespondence(
                        addition.addedObjectIdentity,
                        implementation.identity,
                        IMPLEMENTATION
                    )
                    implementation.implements = byIdentity(addition.addedObjectIdentity)
                }
            }
        }
    }

    private fun TransformationSide.processOpenApiAdditions() {
        for (addition in additions) {
            check(addition.addedObjectClass == Endpoint.Metaclass) { "wtf?" }

        }
    }

    private fun processInterfaceModification(modification: AttributeChange) {
        val javaInterface = javaModel.requireObject(modification.targetObject)
        val implementation = javaModel.requireObject(
            byIdentity(correspondences.requireRightCorrespondence(javaInterface.identity, IMPLEMENTATION))
        ) as Class
        with(Interface.Metaclass.Attributes) {
            when (modification.targetAttribute) {
                name -> implementation.name = javaInterface[name]?.replace("Service", "Server")
                methods -> {// TODO deletion
                    javaInterface[methods]?.forEach { interfaceMethodIdentifier ->
                        val interfaceMethod = javaModel.requireObject(interfaceMethodIdentifier) as Method
                        val correspondingEndpoint =
                            correspondences.getRightCorrespondence(interfaceMethod, INTERFACE_METHOD) as Endpoint?
                        if (correspondingEndpoint == null) createEndpointForMethod(interfaceMethod)
                    }
                    implementation.methods = javaInterface[methods]?.map { interfaceMethodIdentifier ->
                        val interfaceMethod = javaModel.requireObject(interfaceMethodIdentifier) as Method
                        byIdentity(
                            correspondences.getRightCorrespondence(interfaceMethod.identity, IMPLEMENTATION)
                                ?: createImplementationForMethod(interfaceMethod).identity
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
        correspondences.addLeftToRightCorrespondence(javaMethod, endpoint, INTERFACE_METHOD)
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
        correspondences.addLeftToRightCorrespondence(interfaceMethod, implementation, IMPLEMENTATION)
        return implementation
    }

    private fun processMethodModification(modification: AttributeChange) {
        val javaMethod = javaModel.requireObject(modification.targetObject)

        val endpoint = correspondences.getRightCorrespondence(javaMethod, INTERFACE_METHOD) as Endpoint?
        val implementation = correspondences.getRightCorrespondence(javaMethod.identity, IMPLEMENTATION)
            ?.let { javaModel.requireObject(byIdentity(it)) } as Method?

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
                                correspondences.removeCorrespondence(endpoint)
                            }
                        }
                    }
                    processChanges(ChangeSet.EMPTY, nextChanges)
                    if (implementation != null) implementation.visibility = javaMethod[visiblity]
                }
            }
        }
    }

    private fun getEndpointPath(javaMethod: ModelObject): String? {
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

    private fun getEndpointMethod(javaMethod: ModelObject): String? {
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

    companion object Type : BaseModelTransformationType(JavaMetamodel, OpenApiMetamodel) {
        override fun createChecked(
            leftModel: ChangeRecordingModel,
            rightModel: ChangeRecordingModel
        ) = Java2OpenApiTransformation(leftModel, rightModel)
    }

    enum class Tag {
        INTERFACE, INTERFACE_METHOD, IMPLEMENTATION
    }
}