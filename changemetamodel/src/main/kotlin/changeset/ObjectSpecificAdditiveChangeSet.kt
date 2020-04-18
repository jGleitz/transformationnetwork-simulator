package de.joshuagleitze.transformationnetwork.changemetamodel.changeset

import de.joshuagleitze.transformationnetwork.changemetamodel.AdditionChange
import de.joshuagleitze.transformationnetwork.changemetamodel.AttributeChange
import de.joshuagleitze.transformationnetwork.changemetamodel.DeletionChange
import de.joshuagleitze.transformationnetwork.changemetamodel.ModelChange
import de.joshuagleitze.transformationnetwork.changemetamodel.ModelObjectChange
import de.joshuagleitze.transformationnetwork.metametamodel.MetaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.ModelIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObject

class ObjectSpecificAdditiveChangeSet<O : ModelObject<O>> private constructor(
    val targetObject: ModelObject<O>,
    private val _changes: HashMap<MetaAttribute<*>, AttributeChange<O>>
) : AdditiveChangeSet {
    constructor(targetObject: ModelObject<O>) : this(targetObject, HashMap())

    override val deletions: Collection<DeletionChange<O>> get() = emptyList()
    override val additions: Collection<AdditionChange<O>> get() = emptyList()
    override val modifications: Collection<AttributeChange<O>> get() = _changes.values
    override val affectedModels: Set<ModelIdentity>
        get() = targetObject.model?.identity?.let { setOf(it) } ?: emptySet()
    override val size: Int get() = _changes.size

    override fun add(change: ModelChange) = when (change) {
        is AttributeChange<*> -> addAttributeChange(change)
        is ModelObjectChange<*> -> error("Cannot add a ${ModelObjectChange::class.simpleName} to a ${this::class.simpleName}!")
    }

    private fun addAttributeChange(change: AttributeChange<*>): Boolean {
        check(change.targetObject.matches(targetObject)) { "The added change $change does not target this change sets’ target object $targetObject!" }
        @Suppress("UNCHECKED_CAST")
        return _changes.put(change.targetAttribute, change as AttributeChange<O>) != change
    }

    override fun addAll(changes: Collection<ModelChange>) =
        changes.fold(false) { lastResult, change -> add(change) || lastResult }

    override fun addAll(changes: ChangeSet): Boolean {
        check(changes.additions.isEmpty()) { "Cannot add an ${ModelObjectChange::class.simpleName} to a ${this::class.simpleName}!" }
        check(changes.deletions.isEmpty()) { "Cannot add an ${ModelObjectChange::class.simpleName} to a ${this::class.simpleName}!" }
        return changes.modifications.fold(false) { lastResult, change -> addAttributeChange(change) || lastResult }
    }

    override fun copy() = ObjectSpecificAdditiveChangeSet(this.targetObject, HashMap(this._changes))

    override fun filterByModel(modelIdentity: ModelIdentity) =
        if (targetObject.model?.let { modelIdentity.identifies(it) } == true) this else ChangeSet.EMPTY

    override fun contains(element: ModelChange) = when (element) {
        is AttributeChange<*> -> _changes[element.targetAttribute] == element
        else -> false
    }

    override fun containsAll(elements: Collection<ModelChange>) = elements.all { contains(it) }

    override fun isEmpty() = _changes.isEmpty()

    override fun iterator() = _changes.values.iterator()

    override fun equals(other: Any?) = standardEquals(other)
    override fun hashCode() = standardHashCode()
    override fun toString() = standardToString()
}
