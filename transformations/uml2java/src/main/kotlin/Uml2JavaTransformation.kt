package de.joshuagleitze.transformationnetwork.transformations

import de.joshuagleitze.transformationnetwork.changemetamodel.AdditionChange
import de.joshuagleitze.transformationnetwork.changemetamodel.AnyAttributeChange
import de.joshuagleitze.transformationnetwork.changemetamodel.AttributeChange
import de.joshuagleitze.transformationnetwork.changerecording.BaseModelTransformation
import de.joshuagleitze.transformationnetwork.changerecording.BaseModelTransformationType
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecordingModel
import de.joshuagleitze.transformationnetwork.metametamodel.AnyModelObject
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObjectIdentifier
import de.joshuagleitze.transformationnetwork.metametamodel.byIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.ofType
import de.joshuagleitze.transformationnetwork.models.java.JavaMetamodel
import de.joshuagleitze.transformationnetwork.models.uml.UmlMetamodel
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelCorrespondences.CorrespondenceTag
import de.joshuagleitze.transformationnetwork.models.java.Class as JavaClass
import de.joshuagleitze.transformationnetwork.models.java.Classifier as JavaClassifier
import de.joshuagleitze.transformationnetwork.models.java.Interface as JavaInterface
import de.joshuagleitze.transformationnetwork.models.java.Method as JavaMethod
import de.joshuagleitze.transformationnetwork.models.uml.Class as UmlClass
import de.joshuagleitze.transformationnetwork.models.uml.Interface as UmlInterface
import de.joshuagleitze.transformationnetwork.models.uml.Method as UmlMethod
import de.joshuagleitze.transformationnetwork.models.uml.Type as UmlType

class Uml2JavaTransformation(val umlModel: ChangeRecordingModel, val javaModel: ChangeRecordingModel) :
    BaseModelTransformation() {
    override val leftModel: ChangeRecordingModel get() = umlModel
    override val rightModel: ChangeRecordingModel get() = javaModel
    override val type get() = Type

    override fun isConsistent(): Boolean {
        return umlModel.objects.ofType(UmlInterface.Metaclass).all { umlInterface ->
            val javaInterface = correspondences.getRightCorrespondence(umlInterface, INTERFACES)
            javaInterface != null
                    && javaInterface.name == umlInterface.name
                    && javaInterface.methods == umlInterface.methods?.mapNotNull(::getMatchingJavaMethod)
        } && umlModel.objects.ofType(UmlClass.Metaclass).all { umlClass ->
            val javaClass = correspondences.getRightCorrespondence(umlClass, CLASSES)
            javaClass != null
                    && javaClass.name == umlClass.name
                    && javaClass.methods == umlClass.methods?.mapNotNull(::getMatchingJavaMethod)
                    && javaClass.implements == umlClass.implements?.let(umlModel::getObject)
                ?.let { correspondences.getRightCorrespondence(it, INTERFACES) }
                ?.let(::byIdentity)
        } && umlModel.objects.ofType(UmlMethod.Metaclass).all { umlMethod ->
            val javaMethod = correspondences.getRightCorrespondence(umlMethod, METHODS)
            javaMethod != null
                    && javaMethod.name == umlMethod.name
                    && javaMethod.parameters == umlMethod.parameters
                    && javaMethod.visibility == "public"
                    && javaMethod.modifiers?.contains("override") != true
        } && javaModel.objects.ofType(JavaInterface.Metaclass).all { javaInterface ->
            correspondences.getLeftCorrespondence(javaInterface, INTERFACES) != null
        } && javaModel.objects.ofType(JavaClass.Metaclass).all { javaClass ->
            correspondences.getLeftCorrespondence(javaClass, CLASSES) != null
        }
    }

    private fun getMatchingJavaMethod(identifier: ModelObjectIdentifier<UmlMethod>) =
        umlModel.getObject(identifier)?.let { umlMethod ->
            correspondences.getRightCorrespondence(umlMethod, METHODS)
        }?.let(::byIdentity)

    override fun processChangesChecked(leftSide: TransformationSide, rightSide: TransformationSide) {
        leftSide.propagateDeletionsToOtherSide()
        rightSide.propagateDeletionsToOtherSide()

        leftSide.additions.adding(UmlInterface.Metaclass).forEach(::processUmlInterfaceAddition)
        leftSide.additions.adding(UmlClass.Metaclass).forEach(::processUmlClassAddition)
        leftSide.additions.adding(UmlMethod.Metaclass).forEach(::processUmlMethodAddition)

        rightSide.additions.adding(JavaInterface.Metaclass).forEach(::processJavaInterfaceAddition)
        rightSide.additions.adding(JavaClass.Metaclass).forEach(::processJavaClassAddition)

        leftSide.modifications.targetting(UmlInterface.Metaclass).forEach(::processUmlInterfaceModification)
        leftSide.modifications.targetting(UmlClass.Metaclass).forEach(::processUmlClassModification)
        leftSide.modifications.targetting(UmlMethod.Metaclass).forEach(::processUmlMethodModification)

        rightSide.modifications.targetting(JavaInterface.Metaclass).forEach(::processJavaInterfaceModification)
        rightSide.modifications.targetting(JavaClass.Metaclass).forEach(::processJavaClassModification)
        rightSide.modifications.targetting(JavaMethod.Metaclass).forEach(::processJavaMethodModification)
    }

    private fun processUmlInterfaceAddition(addition: AdditionChange<UmlInterface>) {
        val javaInterface = JavaInterface()
        javaModel += javaInterface
        correspondences.addLeftToRightCorrespondence(addition.addedObjectIdentity, javaInterface.identity, INTERFACES)
    }

    private fun processUmlClassAddition(addition: AdditionChange<UmlClass>) {
        val javaClass = JavaClass()
        javaModel += javaClass
        correspondences.addLeftToRightCorrespondence(addition.addedObjectIdentity, javaClass.identity, CLASSES)
    }

    private fun processUmlMethodAddition(addition: AdditionChange<UmlMethod>) {
        val javaMethod = JavaMethod().apply {
            visibility = "public"
            modifiers = emptyList()
        }
        javaModel += javaMethod
        correspondences.addLeftToRightCorrespondence(addition.addedObjectIdentity, javaMethod.identity, METHODS)
    }

    private fun processJavaInterfaceAddition(addition: AdditionChange<JavaInterface>) {
        val umlInterface = UmlInterface()
        umlModel += umlInterface
        correspondences.addLeftToRightCorrespondence(umlInterface.identity, addition.addedObjectIdentity, INTERFACES)
    }

    private fun processJavaClassAddition(addition: AdditionChange<JavaClass>) {
        val umlClass = UmlClass()
        umlModel += umlClass
        correspondences.addLeftToRightCorrespondence(umlClass.identity, addition.addedObjectIdentity, CLASSES)
    }

    private fun processUmlTypeModification(
        modification: AnyAttributeChange,
        umlType: AnyModelObject,
        correspondingJavaClassifier: AnyModelObject
    ) {
        val java = JavaClass.Metaclass.Attributes
        with(UmlClass.Metaclass.Attributes) {
            when (modification.targetAttribute) {
                name -> correspondingJavaClassifier[java.name] = umlType[name]
                methods -> correspondingJavaClassifier[java.methods] = umlType[methods]?.map { umlMethod ->
                    val javaMethod =
                        correspondences.requireRightCorrespondence(umlModel.requireObject(umlMethod), METHODS)
                    if (javaMethod.modifiers?.contains("abstract") != true) {
                        javaMethod.modifiers = (javaMethod.modifiers ?: mutableListOf()) + "abstract"
                    }
                    byIdentity(javaMethod)
                }
            }
        }
    }

    private fun processUmlInterfaceModification(modification: AttributeChange<UmlInterface>) {
        val umlInterface = umlModel.requireObject(modification.targetObject)
        processUmlTypeModification(
            modification,
            umlInterface,
            correspondences.requireRightCorrespondence(umlInterface, INTERFACES)
        )
    }

    private fun processUmlClassModification(modification: AttributeChange<UmlClass>) {
        val umlClass = umlModel.requireObject(modification.targetObject)
        val correspondingJavaClass = correspondences.requireRightCorrespondence(umlClass, CLASSES)
        processUmlTypeModification(modification, umlClass, correspondingJavaClass)

        with(UmlClass.Metaclass.Attributes) {
            when (modification.targetAttribute) {
                implements -> correspondingJavaClass.implements =
                    umlClass.implements?.let { umlInterface ->
                        val javaInterface = correspondences.requireRightCorrespondence(
                            umlModel.requireObject(umlInterface),
                            INTERFACES
                        )
                        byIdentity(javaInterface)
                    }
            }
        }
    }

    private fun processJavaClassifierModification(
        modification: AnyAttributeChange,
        javaClassifier: AnyModelObject,
        correspondingUmlType: AnyModelObject
    ) {
        val uml = UmlType.Metaclass.Attributes
        with(JavaClassifier.Metaclass.Attributes) {
            when (modification.targetAttribute) {
                name -> correspondingUmlType[uml.name] = javaClassifier[name]
                methods -> correspondingUmlType[uml.methods] = javaClassifier[methods]?.mapNotNull {
                    val javaMethod = javaModel.requireObject(it)
                    if (javaMethod.modifiers?.contains("override") == true) null
                    else byIdentity(correspondences.requireLeftCorrespondence(javaMethod, METHODS).identity)
                }
            }
        }
    }

    private fun processJavaInterfaceModification(modification: AttributeChange<JavaInterface>) {
        val javaInterface = javaModel.requireObject(modification.targetObject)
        processJavaClassifierModification(
            modification,
            javaInterface,
            correspondences.requireLeftCorrespondence(javaInterface, INTERFACES)
        )
    }

    private fun processJavaClassModification(modification: AttributeChange<JavaClass>) {
        val javaClass = javaModel.requireObject(modification.targetObject)
        val correspondingUmlClass = correspondences.requireLeftCorrespondence(javaClass, CLASSES)
        processJavaClassifierModification(modification, javaClass, correspondingUmlClass)

        with(JavaClass.Metaclass.Attributes) {
            when (modification.targetAttribute) {
                implements -> correspondingUmlClass.implements =
                    javaClass.implements?.let { javaInterface ->
                        val umlInterface = correspondences.requireLeftCorrespondence(
                            javaModel.requireObject(javaInterface),
                            INTERFACES
                        )
                        byIdentity(umlInterface)
                    }
            }
        }
    }

    private fun processUmlMethodModification(modification: AttributeChange<UmlMethod>) {
        val umlMethod = umlModel.requireObject(modification.targetObject)
        val correspondingJavaMethod = correspondences.requireRightCorrespondence(umlMethod, METHODS)
        with(UmlMethod.Metaclass.Attributes) {
            when (modification.targetAttribute) {
                name -> correspondingJavaMethod.name = umlMethod.name
                parameters -> correspondingJavaMethod.parameters = umlMethod.parameters
            }
        }
    }

    private fun processJavaMethodModification(modification: AttributeChange<JavaMethod>) {
        val javaMethod = javaModel.requireObject(modification.targetObject)
        var umlMethod = correspondences.getLeftCorrespondence(javaMethod, METHODS)
        if (javaMethod.modifiers?.contains("override") != true && umlMethod == null) {
            umlMethod = UmlMethod()
            umlModel += umlMethod
            correspondences.addLeftToRightCorrespondence(umlMethod, javaMethod, METHODS)
        }
        umlMethod?.let { correspondingUmlMethod ->
            with(JavaMethod.Metaclass.Attributes) {
                when (modification.targetAttribute) {
                    name -> correspondingUmlMethod.name = javaMethod.name
                    parameters -> correspondingUmlMethod.parameters = javaMethod.parameters
                }
                if (javaMethod.visibility != "public" || javaMethod.modifiers?.contains("override") == true) {
                    umlModel.removeObject(correspondingUmlMethod)
                    correspondences.removeCorrespondence(umlMethod, METHODS)
                }
            }
        }
    }

    companion object Type : BaseModelTransformationType(UmlMetamodel, JavaMetamodel) {
        override fun createChecked(leftModel: ChangeRecordingModel, rightModel: ChangeRecordingModel) =
            Uml2JavaTransformation(leftModel, rightModel)

        private val INTERFACES = CorrespondenceTag(UmlInterface.Metaclass, JavaInterface.Metaclass)
        private val CLASSES = CorrespondenceTag(UmlClass.Metaclass, JavaClass.Metaclass)
        private val METHODS = CorrespondenceTag(UmlMethod.Metaclass, JavaMethod.Metaclass)
    }
}
