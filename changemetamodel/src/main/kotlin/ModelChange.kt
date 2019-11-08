package de.joshuagleitze.transformationnetwork.changemetamodel

import de.joshuagleitze.transformationnetwork.metametamodel.MetaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObject

sealed class ModelChange(val targetModel: Model)

sealed class ObjectChange(targetModel: Model) : ModelChange(targetModel)

sealed class AttributeChange(targetModel: Model, val targetObject: ModelObject) :
    ModelChange(targetModel) {
    abstract val targetAttribute: MetaAttribute<*>
}

class AdditionChange(targetModel: Model, val addedObject: ModelObject) : ObjectChange(targetModel), RevertibleChange {
    init {
        checkModelObjectMembership(addedObject)
    }

    override fun revert() {
        targetModel -= addedObject
    }
}

class DeletionChange(targetModel: Model, val deletedObject: ModelObject) : ObjectChange(targetModel), RevertibleChange {
    init {
        checkModelObjectType(deletedObject)
    }

    override fun revert() {
        targetModel += deletedObject
    }
}

class AttributeSetChange<T : Any>(
    targetModel: Model,
    targetObject: ModelObject,
    override val targetAttribute: MetaAttribute<T>,
    val oldValue: T?
) : AttributeChange(targetModel, targetObject), RevertibleChange {
    val newValue: T?

    init {
        checkModelObjectType(targetObject)
        targetObject.checkMetaAttributeInMetaclass(targetAttribute)
        targetAttribute.checkValueType(oldValue)
        newValue = targetObject[targetAttribute]
    }

    override fun revert() {
        targetObject[targetAttribute] = oldValue
    }
}


private fun ModelChange.checkModelObjectType(modelObject: ModelObject) {
    check(targetModel.metamodel.classes.contains(modelObject.metaclass)) { "$modelObject’s metaclass '${modelObject.metaclass}' does not belong to the target model’s metamodel '${targetModel.metamodel}'!" }
}

private fun ModelChange.checkModelObjectMembership(modelObject: ModelObject) {
    check(targetModel.objects.contains(modelObject)) { "the target model '$targetModel' does not contain the model object '$modelObject'!" }
}

private fun ModelObject.checkMetaAttributeInMetaclass(attribute: MetaAttribute<*>) {
    check(metaclass.attributes.contains(attribute)) { "'$attribute' is not an attribute of the target objects’s metaclass '${this.metaclass}" }
}

private fun <T : Any> MetaAttribute<T>.checkValueType(value: T?) {
    if (value != null) {
        check(elementType.isInstance(value)) { "the value '$value' for $this has wrong type ${value::class} instead of expected $elementType!" }
    }
}