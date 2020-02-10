package de.joshuagleitze.transformationnetwork.changemetamodel

import de.joshuagleitze.transformationnetwork.metametamodel.MetaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.Metaclass
import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.metametamodel.ModelIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObjectIdentifier
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObjectIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.newObjectIdentity

sealed class ModelChange(val targetModel: ModelIdentity) {
    abstract fun applyTo(model: Model)
}

sealed class ModelObjectChange(targetModel: ModelIdentity) : ModelChange(targetModel)

sealed class AttributeChange(targetModel: ModelIdentity, val targetObject: ModelObjectIdentifier) :
    ModelChange(targetModel) {
    abstract val targetAttribute: MetaAttribute<*>
}

class AdditionChange(
    targetModel: ModelIdentity,
    addedObjectClass: Metaclass,
    val addedObjectIdentity: ModelObjectIdentity = newObjectIdentity(addedObjectClass)
) : ModelObjectChange(targetModel) {
    val addedObjectClass: Metaclass get() = addedObjectIdentity.metaclass

    init {
        checkModelObjectType(addedObjectClass)
    }

    override fun applyTo(model: Model) {
        checkTargetModel(model)
        model += addedObjectClass.createNew(addedObjectIdentity)
    }

    override fun toString() = "$targetModel += new ${addedObjectClass.name}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as AdditionChange

        if (targetModel != other.targetModel) return false
        if (addedObjectIdentity != other.addedObjectIdentity) return false

        return true
    }

    override fun hashCode(): Int {
        var result = addedObjectIdentity.hashCode()
        result = 31 * result + targetModel.hashCode()
        return result
    }
}

class DeletionChange(targetModel: ModelIdentity, val deletedObject: ModelObjectIdentifier) :
    ModelObjectChange(targetModel) {
    init {
        checkModelObjectType(deletedObject)
    }

    override fun applyTo(model: Model) {
        checkTargetModel(model)
        model -= model.applicationObject
    }

    private val Model.applicationObject get() = checkNotNull(this.getObject(deletedObject)) { "Cannot find the target object '$deletedObject' in the model!" }

    override fun toString() = "$targetModel -= $deletedObject"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as DeletionChange

        if (targetModel != other.targetModel) return false
        if (deletedObject != other.deletedObject) return false

        return true
    }

    override fun hashCode(): Int {
        var result = deletedObject.hashCode()
        result = 31 * result + targetModel.hashCode()
        return result
    }
}

class AttributeSetChange<T : Any> private constructor(
    targetModel: ModelIdentity,
    targetObject: ModelObjectIdentifier,
    override val targetAttribute: MetaAttribute<T>,
    newValue: T? = null,
    private var newValueSet: Boolean = false
) : AttributeChange(targetModel, targetObject) {
    val newValue = newValue
        get() {
            check(newValueSet) { "No new value was provided for this change!" }
            return field
        }

    init {
        checkModelObjectType(targetObject)
        targetObject.checkMetaAttributeInMetaclass(targetAttribute)
        targetAttribute.checkCanBeValue(newValue)
    }

    constructor(
        targetModel: ModelIdentity,
        targetObject: ModelObjectIdentifier,
        targetAttribute: MetaAttribute<T>,
        newValue: T?
    ) : this(targetModel, targetObject, targetAttribute, newValue = newValue, newValueSet = true)

    override fun applyTo(model: Model) {
        checkTargetModel(model)
        model.applicationObject[targetAttribute] = newValue
    }

    private val Model.applicationObject get() = checkNotNull(this.getObject(targetObject)) { "Cannot find the target object '$targetObject' in the model '$this'!" }

    override fun toString() = "$targetObject[$targetAttribute] = $newValue"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as AttributeSetChange<*>

        if (targetModel != other.targetModel) return false
        if (targetObject != other.targetObject) return false
        if (targetAttribute != other.targetAttribute) return false
        if (newValue != other.newValue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = targetAttribute.hashCode()
        result = 31 * result + targetModel.hashCode()
        result = 31 * result + targetObject.hashCode()
        result = 31 * result + newValue.hashCode()
        return result
    }
}

private fun ModelChange.checkTargetModel(model: Model) {
    check(model.metamodel == targetModel.metamodel) { "This change targets the metamodel '${targetModel.metamodel}. The supplied model has the metamodel '${model.metamodel}!" }
    check(targetModel.identifies(model)) { "This change targets another logical instance of ${targetModel.metamodel}!" }
}

private fun ModelChange.checkModelObjectType(modelObjectClass: Metaclass) {
    check(targetModel.metamodel.classes.contains(modelObjectClass)) { "The metaclass '$modelObjectClass' does not belong to the target model’s metamodel '${targetModel.metamodel}'!" }
}

private fun ModelChange.checkModelObjectType(modelObject: ModelObjectIdentifier) {
    check(targetModel.metamodel.classes.contains(modelObject.metaclass)) { "$modelObject’s metaclass '${modelObject.metaclass}' does not belong to the target model’s metamodel '${targetModel.metamodel}'!" }
}

private fun ModelObjectIdentifier.checkMetaAttributeInMetaclass(attribute: MetaAttribute<*>) {
    check(metaclass.attributes.contains(attribute)) { "'$attribute' is not an attribute of the target objects’s metaclass '${this.metaclass}" }
}