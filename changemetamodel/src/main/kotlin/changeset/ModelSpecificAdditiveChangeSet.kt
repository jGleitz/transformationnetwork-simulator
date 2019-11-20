package de.joshuagleitze.transformationnetwork.changemetamodel.changeset

import de.joshuagleitze.transformationnetwork.changemetamodel.AdditionChange
import de.joshuagleitze.transformationnetwork.changemetamodel.AttributeChange
import de.joshuagleitze.transformationnetwork.changemetamodel.DeletionChange
import de.joshuagleitze.transformationnetwork.changemetamodel.ModelChange
import de.joshuagleitze.transformationnetwork.metametamodel.ModelIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObject

class ModelSpecificAdditiveChangeSet private constructor(
    val targetModel: ModelIdentity,
    private val _additions: MutableMap<ModelObject, AdditionChange>,
    private val _deletions: MutableMap<ModelObject, DeletionChange>
) : AdditiveChangeSet {
    constructor(targetModel: ModelIdentity) : this(targetModel, HashMap(), HashMap())

    override val affectedModels: Set<ModelIdentity> get() = if (isEmpty()) emptySet() else setOf(targetModel)
    override val additions: Collection<AdditionChange> get() = _additions.values
    override val deletions: Collection<DeletionChange> get() = _deletions.values
    override val modifications: Collection<AttributeChange> get() = emptyList()
    override val size: Int get() = _additions.size + _deletions.size

    override fun add(change: ModelChange): Boolean {
        check(change.targetModel == this.targetModel) { "The change’s '$change' target model is not this change set’s target model!" }
        return when (change) {
            is AdditionChange -> addAdditionChange(change)
            is DeletionChange -> addDeletionChange(change)
            is AttributeChange -> error("Cannot add an ${AttributeChange::class.simpleName} to a ${this::class.simpleName}!")
        }
    }

    private fun addAdditionChange(change: AdditionChange): Boolean {
        if (!_deletions.containsKey(change.addedObject)) {
            return _additions.put(change.addedObject, change) == null
        }
        return false
    }

    private fun addDeletionChange(change: DeletionChange): Boolean {
        _additions -= change.deletedObject
        return _deletions.put(change.deletedObject, change) == null
    }

    override fun addAll(changes: Collection<ModelChange>) =
        changes.fold(false) { lastResult, change -> add(change) || lastResult }

    override fun addAll(changes: ChangeSet): Boolean {
        check(changes.modifications.isEmpty()) { "Cannot add an ${AttributeChange::class.simpleName} to a ${this::class.simpleName}!" }
        val additionResult =
            changes.additions.fold(false) { lastResult, change -> addAdditionChange(change) || lastResult }
        val deletionResult =
            changes.deletions.fold(false) { lastResult, change -> addDeletionChange(change) || lastResult }
        return additionResult || deletionResult
    }

    override fun contains(element: ModelChange) = when (element) {
        is AdditionChange -> _additions[element.addedObject] == element
        is DeletionChange -> _deletions[element.deletedObject] == element
        is AttributeChange -> false
    }

    override fun containsAll(elements: Collection<ModelChange>) = elements.all { contains(it) }

    override fun isEmpty() = _additions.isEmpty() && _deletions.isEmpty()

    override fun filterByModel(modelIdentity: ModelIdentity) =
        if (modelIdentity == targetModel) this else ChangeSet.EMPTY

    override fun copy() =
        ModelSpecificAdditiveChangeSet(
            this.targetModel,
            HashMap(this._additions),
            HashMap(this._deletions)
        )

    override fun iterator(): Iterator<ModelChange> {
        val addedIterator = _additions.values.iterator()
        val deletedIterator = _deletions.values.iterator()
        return object : Iterator<ModelChange> {
            override fun hasNext() = addedIterator.hasNext() || deletedIterator.hasNext()

            override fun next() = when {
                addedIterator.hasNext() -> addedIterator
                else -> deletedIterator
            }.next()
        }
    }

    override fun equals(other: Any?) = standardEquals(other)
    override fun hashCode() = standardHashCode()
    override fun toString() = standardToString()
}