package de.joshuagleitze.transformationnetwork.transformations

import de.joshuagleitze.transformationnetwork.changemetamodel.AttributeChange
import de.joshuagleitze.transformationnetwork.changerecording.BaseModelTransformation
import de.joshuagleitze.transformationnetwork.changerecording.BaseModelTransformationType
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecordingModel
import de.joshuagleitze.transformationnetwork.metametamodel.byIdentity
import de.joshuagleitze.transformationnetwork.models.java.JavaMetamodel
import de.joshuagleitze.transformationnetwork.models.uml.UmlMetamodel
import de.joshuagleitze.transformationnetwork.models.java.Class as JavaClass
import de.joshuagleitze.transformationnetwork.models.java.Classifier as JavaClassifier
import de.joshuagleitze.transformationnetwork.models.java.Interface as JavaInterface
import de.joshuagleitze.transformationnetwork.models.java.Method as JavaMethod
import de.joshuagleitze.transformationnetwork.models.uml.Class as UmlClass
import de.joshuagleitze.transformationnetwork.models.uml.Interface as UmlInterface
import de.joshuagleitze.transformationnetwork.models.uml.Method as UmlMethod
import de.joshuagleitze.transformationnetwork.models.uml.Type as UmlType

private const val MILLIS_IN_YEAR = 1_000L * 60L * 60L * 24L * 365L

class Uml2JavaTransformation(val umlModel: ChangeRecordingModel, val javaModel: ChangeRecordingModel) :
    BaseModelTransformation<Nothing>() {
    override val leftModel: ChangeRecordingModel get() = umlModel
    override val rightModel: ChangeRecordingModel get() = javaModel
    override val type get() = Type

    override fun processChangesChecked(leftSide: TransformationSide, rightSide: TransformationSide) {
        leftSide.propagateDeletionsToOtherSide()
        rightSide.propagateDeletionsToOtherSide()
        leftSide.processUmlAdditions()
        rightSide.processJavaAdditions()
        leftSide.modifications.executeFiltered(UmlInterface.Metaclass, ::processUmlInterfaceModification)
        leftSide.modifications.executeFiltered(UmlClass.Metaclass, ::processUmlClassModification)
        leftSide.modifications.executeFiltered(UmlMethod.Metaclass, ::processUmlMethodModification)
        rightSide.modifications.executeFiltered(JavaInterface.Metaclass, ::processJavaInterfaceModification)
        rightSide.modifications.executeFiltered(JavaClass.Metaclass, ::processJavaClassModification)
        rightSide.modifications.executeFiltered(JavaMethod.Metaclass, ::processJavaMethodModification)
    }

    private fun TransformationSide.processUmlAdditions() {
        for (addition in additions) {
            val javaObject = when (addition.addedObjectClass) {
                UmlInterface.Metaclass -> JavaInterface()
                UmlClass.Metaclass -> JavaClass()
                UmlMethod.Metaclass -> JavaMethod().apply {
                    visibility = "public"
                    modifiers = emptyList()
                }
                else -> error("unknown UML object type: ${addition.addedObjectClass}")
            }
            javaModel += javaObject
            correspondences.addLeftToRightCorrespondence(addition.addedObjectIdentity, javaObject.identity)
        }
    }

    private fun TransformationSide.processJavaAdditions() {
        for (addition in additions) {
            val umlObject = when (addition.addedObjectClass) {
                JavaInterface.Metaclass -> UmlInterface()
                JavaClass.Metaclass -> UmlClass()
                JavaMethod.Metaclass -> null
                else -> error("unknown Java object type: ${addition.addedObjectClass}")
            }
            if (umlObject != null) {
                umlModel += umlObject
                correspondences.addLeftToRightCorrespondence(umlObject.identity, addition.addedObjectIdentity)
            }
        }
    }

    private fun processUmlTypeModification(modification: AttributeChange) {
        val umlType = umlModel.requireObject(modification.targetObject)
        val correspondingJavaClassifier = correspondences.requireRightCorrespondence(umlType)
        val java = JavaClass.Metaclass.Attributes
        with(UmlClass.Metaclass.Attributes) {
            when (modification.targetAttribute) {
                name -> correspondingJavaClassifier[java.name] = umlType[name]
                methods -> correspondingJavaClassifier[java.methods] = umlType[methods]?.map {
                    val javaMethod =
                        correspondences.requireRightCorrespondence(umlModel.requireObject(it)) as JavaMethod
                    if (javaMethod.modifiers?.contains("abstract") != true) {
                        javaMethod.modifiers = (javaMethod.modifiers ?: mutableListOf()) + "abstract"
                    }
                    byIdentity(correspondences.requireRightCorrespondence(umlModel.requireObject(it)).identity)
                }
            }
        }
    }

    private fun processUmlInterfaceModification(modification: AttributeChange) {
        processUmlTypeModification(modification)
    }

    private fun processUmlClassModification(modification: AttributeChange) {
        processUmlTypeModification(modification)
        val umlClass = umlModel.requireObject(modification.targetObject)
        val correspondingJavaClass = correspondences.requireRightCorrespondence(umlClass) as JavaClass

        with(UmlClass.Metaclass.Attributes) {
            when (modification.targetAttribute) {
                implements -> correspondingJavaClass.implements =
                    umlClass[implements]?.let {
                        byIdentity(correspondences.requireRightCorrespondence(javaModel.requireObject(it).identity))
                    }
            }
        }
    }

    private fun processJavaClassifierModification(modification: AttributeChange) {
        val javaClassifier = javaModel.requireObject(modification.targetObject)
        val correspondingUmlType = correspondences.requireLeftCorrespondence(javaClassifier)
        val uml = UmlType.Metaclass.Attributes
        with(JavaClassifier.Metaclass.Attributes) {
            when (modification.targetAttribute) {
                name -> correspondingUmlType[uml.name] = javaClassifier[name]
                methods -> correspondingUmlType[uml.methods] = javaClassifier[methods]?.mapNotNull {
                    val javaMethod = javaModel.requireObject(it) as JavaMethod
                    if (javaMethod.modifiers?.contains("override") == true) null
                    else byIdentity(correspondences.requireLeftCorrespondence(javaMethod).identity)
                }
            }
        }
    }

    private fun processJavaInterfaceModification(modification: AttributeChange) {
        processJavaClassifierModification(modification)
    }

    private fun processJavaClassModification(modification: AttributeChange) {
        processJavaClassifierModification(modification)
        val javaClass = javaModel.requireObject(modification.targetObject)
        val correspondingUmlClass = correspondences.requireLeftCorrespondence(javaClass) as UmlClass

        with(JavaClass.Metaclass.Attributes) {
            when (modification.targetAttribute) {
                implements -> correspondingUmlClass.implements =
                    javaClass[implements]?.let {
                        byIdentity(correspondences.requireLeftCorrespondence(javaModel.requireObject(it).identity))
                    }
            }
        }
    }

    private fun processUmlMethodModification(modification: AttributeChange) {
        val umlMethod = umlModel.requireObject(modification.targetObject)
        val correspondingJavaMethod = correspondences.requireRightCorrespondence(umlMethod) as JavaMethod
        with(UmlMethod.Metaclass.Attributes) {
            when (modification.targetAttribute) {
                name -> correspondingJavaMethod.name = umlMethod[name]
                parameters -> correspondingJavaMethod.parameters = umlMethod[parameters]
            }
        }
    }

    private fun processJavaMethodModification(modification: AttributeChange) {
        val javaMethod = javaModel.requireObject(modification.targetObject) as JavaMethod
        var umlMethod = correspondences.getLeftCorrespondence(javaMethod) as UmlMethod?
        if (javaMethod.modifiers?.contains("override") != true && umlMethod == null) {
            umlMethod = UmlMethod()
            umlModel += umlMethod
            correspondences.addLeftToRightCorrespondence(umlMethod, javaMethod)
        }
        umlMethod?.let { correspondingUmlMethod ->
            with(JavaMethod.Metaclass.Attributes) {
                when (modification.targetAttribute) {
                    name -> correspondingUmlMethod.name = javaMethod[name]
                    parameters -> correspondingUmlMethod.parameters = javaMethod[parameters]
                }
                if (javaMethod.visibility != "public" || javaMethod.modifiers?.contains("override") == true) {
                    umlModel.removeObject(correspondingUmlMethod)
                    correspondences.removeCorrespondence(umlMethod)
                }
            }
        }
    }

    companion object Type : BaseModelTransformationType(UmlMetamodel, JavaMetamodel) {
        override fun createChecked(leftModel: ChangeRecordingModel, rightModel: ChangeRecordingModel) =
            Uml2JavaTransformation(leftModel, rightModel)
    }
}