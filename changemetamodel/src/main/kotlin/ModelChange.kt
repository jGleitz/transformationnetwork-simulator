package de.joshuagleitze.transformationnetwork.changemetamodel

import de.joshuagleitze.transformationnetwork.metametamodel.AnyMetaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.AnyMetaclass
import de.joshuagleitze.transformationnetwork.metametamodel.AnyModelObjectIdentifier
import de.joshuagleitze.transformationnetwork.metametamodel.MetaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.Metaclass
import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.metametamodel.ModelIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObject
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObjectIdentifier
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObjectIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.newObjectIdentity

sealed class ModelChange(val targetModel: ModelIdentity) {
    abstract fun applyTo(model: Model)
}

typealias AnyModelObjectChange = ModelObjectChange<*>

sealed class ModelObjectChange<O : ModelObject<O>>(targetModel: ModelIdentity) : ModelChange(targetModel)

typealias AnyAttributeChange = AttributeChange<*>

sealed class AttributeChange<O : ModelObject<O>>(
    targetModel: ModelIdentity,
    val targetObject: ModelObjectIdentifier<O>
) : ModelChange(targetModel) {
    abstract val targetAttribute: MetaAttribute<*>
}

class AdditionChange<O : ModelObject<O>>(
    targetModel: ModelIdentity,
    addedObjectClass: Metaclass<O>,
    val addedObjectIdentity: ModelObjectIdentity<O> = newObjectIdentity(addedObjectClass)
) : ModelObjectChange<O>(targetModel) {
    val addedObjectClass: Metaclass<O> get() = addedObjectIdentity.metaclass

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

        other as AdditionChange<*>

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

class DeletionChange<O : ModelObject<O>>(
    targetModel: ModelIdentity,
    val deletedObject: ModelObjectIdentifier<O>
) : ModelObjectChange<O>(targetModel) {
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

        other as DeletionChange<*>

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

typealias AnyAttributeSetChange = AttributeSetChange<*, *>

class AttributeSetChange<O : ModelObject<O>, T : Any> private constructor(
    targetModel: ModelIdentity,
    targetObject: ModelObjectIdentifier<O>,
    override val targetAttribute: MetaAttribute<T>,
    newValue: T? = null,
    private var newValueSet: Boolean = false
) : AttributeChange<O>(targetModel, targetObject) {
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
        targetObject: ModelObjectIdentifier<O>,
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

        other as AttributeSetChange<*, *>

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

private fun ModelChange.checkModelObjectType(modelObjectClass: AnyMetaclass) {
    check(targetModel.metamodel.classes.contains(modelObjectClass)) { "The metaclass '$modelObjectClass' does not belong to the target model’s metamodel '${targetModel.metamodel}'!" }
}

private fun ModelChange.checkModelObjectType(modelObject: AnyModelObjectIdentifier) {
    check(targetModel.metamodel.classes.contains(modelObject.metaclass)) { "$modelObject’s metaclass '${modelObject.metaclass}' does not belong to the target model’s metamodel '${targetModel.metamodel}'!" }
}

private fun AnyModelObjectIdentifier.checkMetaAttributeInMetaclass(attribute: AnyMetaAttribute) {
    check(metaclass.attributes.contains(attribute)) { "'$attribute' is not an attribute of the target objects’s metaclass '${this.metaclass}" }
}
