package de.joshuagleitze.transformationnetwork.changemetamodel

import de.joshuagleitze.transformationnetwork.metametamodel.MetaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.metametamodel.ModelIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObject

sealed class ModelChange(val targetModel: ModelIdentity) {
    abstract fun revertOn(model: Model)
    abstract fun applyTo(model: Model)
}

sealed class ModelObjectChange(targetModel: ModelIdentity) : ModelChange(targetModel)

sealed class AttributeChange(targetModel: ModelIdentity, val targetObject: ModelObject) : ModelChange(targetModel) {
    abstract val targetAttribute: MetaAttribute<*>
}

class AdditionChange(targetModel: ModelIdentity, val addedObject: ModelObject) : ModelObjectChange(targetModel) {
    init {
        checkModelObjectType(addedObject)
    }

    override fun applyTo(model: Model) {
        checkTargetModel(model)
        model += addedObject
    }

    override fun revertOn(model: Model) {
        checkTargetModel(model)
        model -= addedObject
    }

    override fun toString() = "$targetModel += $addedObject"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as AdditionChange

        if (targetModel != other.targetModel) return false
        if (addedObject != other.addedObject) return false

        return true
    }

    override fun hashCode(): Int {
        var result = addedObject.hashCode()
        result = 31 * result + targetModel.hashCode()
        return result
    }
}

class DeletionChange(targetModel: ModelIdentity, val deletedObject: ModelObject) : ModelObjectChange(targetModel) {
    init {
        checkModelObjectType(deletedObject)
    }

    override fun applyTo(model: Model) {
        checkTargetModel(model)
        model -= deletedObject
    }

    override fun revertOn(model: Model) {
        checkTargetModel(model)
        model += deletedObject
    }

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
    targetObject: ModelObject,
    override val targetAttribute: MetaAttribute<T>,
    oldValue: T? = null,
    newValue: T? = null,
    private var oldValueInited: Boolean = false,
    private var newValueInited: Boolean = false
) : AttributeChange(targetModel, targetObject) {
    private var _newValue = newValue
    private var _oldValue = oldValue
    val newValue get() = _newValue
    val oldValue get() = _oldValue

    init {
        checkModelObjectType(targetObject)
        targetObject.checkMetaAttributeInMetaclass(targetAttribute)
        targetAttribute.checkValueType(oldValue)
        targetAttribute.checkValueType(newValue)
    }

    constructor(
        targetModel: ModelIdentity,
        targetObject: ModelObject,
        targetAttribute: MetaAttribute<T>,
        newValue: T?
    ) : this(targetModel, targetObject, targetAttribute, newValue = newValue, newValueInited = true)

    constructor(
        targetModel: ModelIdentity,
        targetObject: ModelObject,
        targetAttribute: MetaAttribute<T>,
        oldValue: T?,
        newValue: T?
    ) : this(
        targetModel,
        targetObject,
        targetAttribute,
        oldValue = oldValue,
        oldValueInited = true,
        newValue = newValue,
        newValueInited = true
    )

    override fun applyTo(model: Model) {
        checkTargetModel(model)
        check(newValueInited) { "The new value is not set (yet)!" }
        val applicationObject = model.applicationObject
        if (!oldValueInited) {
            _oldValue = applicationObject[targetAttribute]
        }
        applicationObject[targetAttribute] = newValue
    }

    override fun revertOn(model: Model) {
        checkTargetModel(model)
        check(oldValueInited) { "The old value is not set (yet)!" }
        val applicationObject = model.applicationObject
        if (!newValueInited) {
            _newValue = applicationObject[targetAttribute]
        }
        applicationObject[targetAttribute] = oldValue
    }

    private val Model.applicationObject get() = checkNotNull(this.getSameValuedObject(targetObject)) { "Cannot find the target object '$targetObject' in the model!" }

    override fun toString() = "$targetObject[$targetAttribute] = $newValue"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as AttributeSetChange<*>

        if (targetModel != other.targetModel) return false
        if (targetObject != other.targetObject) return false
        if (targetAttribute != other.targetAttribute) return false
        if (oldValue != other.oldValue) return false
        if (newValue != other.newValue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = targetAttribute.hashCode()
        result = 31 * result + targetModel.hashCode()
        result = 31 * result + targetObject.hashCode()
        result = 31 * result + oldValue.hashCode()
        result = 31 * result + newValue.hashCode()
        return result
    }
}

private fun ModelChange.checkTargetModel(model: Model) {
    check(model.metamodel == targetModel.metamodel) { "This change targets the metamodel '${targetModel.metamodel}. The supplied model has the metamodel '${model.metamodel}!" }
    check(targetModel.identifies(model)) { "This change targets another logical instance of ${targetModel.metamodel}!" }
}

private fun ModelChange.checkModelObjectType(modelObject: ModelObject) {
    check(targetModel.metamodel.classes.contains(modelObject.metaclass)) { "$modelObject’s metaclass '${modelObject.metaclass}' does not belong to the target model’s metamodel '${targetModel.metamodel}'!" }
}

private fun ModelObject.checkMetaAttributeInMetaclass(attribute: MetaAttribute<*>) {
    check(metaclass.attributes.contains(attribute)) { "'$attribute' is not an attribute of the target objects’s metaclass '${this.metaclass}" }
}

private fun <T : Any> MetaAttribute<T>.checkValueType(value: T?) {
    if (value != null) {
        check(elementType.isInstance(value)) { "the value '$value' for $this has wrong type ${value::class} instead of expected $elementType!" }
    }
}