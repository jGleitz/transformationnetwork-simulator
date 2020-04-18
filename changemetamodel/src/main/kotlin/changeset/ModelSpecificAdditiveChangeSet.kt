package de.joshuagleitze.transformationnetwork.changemetamodel.changeset

import de.joshuagleitze.transformationnetwork.changemetamodel.AdditionChange
import de.joshuagleitze.transformationnetwork.changemetamodel.AttributeChange
import de.joshuagleitze.transformationnetwork.changemetamodel.DeletionChange
import de.joshuagleitze.transformationnetwork.changemetamodel.ModelChange
import de.joshuagleitze.transformationnetwork.changemetamodel.util.plus
import de.joshuagleitze.transformationnetwork.metametamodel.ModelIdentity

class ModelSpecificAdditiveChangeSet private constructor(
    val targetModel: ModelIdentity,
    private val _additions: MutableSet<AdditionChange<*>>,
    private val _deletions: MutableSet<DeletionChange<*>>,
    private val _modifications: MutableSet<AttributeChange<*>>
) : AdditiveChangeSet {
    constructor(targetModel: ModelIdentity) : this(targetModel, HashSet(), HashSet(), HashSet())

    override val affectedModels: Set<ModelIdentity> get() = if (isEmpty()) emptySet() else setOf(targetModel)
    override val additions: Collection<AdditionChange<*>> get() = _additions
    override val deletions: Collection<DeletionChange<*>> get() = _deletions
    override val modifications: Collection<AttributeChange<*>> get() = _modifications
    override val size: Int get() = _additions.size + _deletions.size + _modifications.size

    override fun add(change: ModelChange): Boolean {
        check(change.targetModel == this.targetModel) { "The change’s '$change' target model is not this change set’s target model!" }
        return when (change) {
            is AdditionChange<*> -> _additions.add(change)
            is DeletionChange<*> -> _deletions.add(change)
            is AttributeChange<*> -> _modifications.add(change)
        }
    }

    override fun addAll(changes: Collection<ModelChange>) =
        changes.fold(false) { lastResult, change -> add(change) || lastResult }

    override fun addAll(changes: ChangeSet): Boolean {
        val additionResult = _additions.addAll(changes.additions)
        val deletionResult = _deletions.addAll(changes.deletions)
        val modificationResult = _modifications.addAll(changes.modifications)
        return additionResult || deletionResult || modificationResult
    }

    override fun contains(element: ModelChange) = when (element) {
        is AdditionChange<*> -> _additions
        is DeletionChange<*> -> _deletions
        is AttributeChange<*> -> _modifications
    }.contains(element)

    override fun containsAll(elements: Collection<ModelChange>) = elements.all { contains(it) }

    override fun isEmpty() = _additions.isEmpty() && _deletions.isEmpty() && _modifications.isEmpty()

    override fun filterByModel(modelIdentity: ModelIdentity) =
        if (modelIdentity == targetModel) this else ChangeSet.EMPTY

    override fun copy() =
        ModelSpecificAdditiveChangeSet(
            this.targetModel,
            HashSet(this._additions),
            HashSet(this._deletions),
            HashSet(this._modifications)
        )

    override fun iterator(): Iterator<ModelChange> =
        _additions.iterator() + _deletions.iterator() + _modifications.iterator()

    override fun equals(other: Any?) = standardEquals(other)
    override fun hashCode() = standardHashCode()
    override fun toString() = standardToString()
}
