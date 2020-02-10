package de.joshuagleitze.transformationnetwork.changemetamodel.changeset

import de.joshuagleitze.transformationnetwork.changemetamodel.AdditionChange
import de.joshuagleitze.transformationnetwork.changemetamodel.AttributeChange
import de.joshuagleitze.transformationnetwork.changemetamodel.DeletionChange
import de.joshuagleitze.transformationnetwork.changemetamodel.ModelChange
import de.joshuagleitze.transformationnetwork.changemetamodel.util.FlatMapIterator
import de.joshuagleitze.transformationnetwork.metametamodel.ModelIdentity

class DefaultAdditiveChangeSet private constructor(
    private val _modelChangeSets: MutableMap<ModelIdentity, ModelSpecificAdditiveChangeSet>
) : AdditiveChangeSet {
    constructor() : this(HashMap())

    override val affectedModels: Set<ModelIdentity> get() = _modelChangeSets.values.flatMapTo(HashSet()) { it.affectedModels }
    override val additions: Collection<AdditionChange> get() = _modelChangeSets.values.flatMap { it.additions }
    override val deletions: Collection<DeletionChange> get() = _modelChangeSets.values.flatMap { it.deletions }
    override val modifications: Collection<AttributeChange> get() = _modelChangeSets.values.flatMap { it.modifications }
    override val size: Int get() = _modelChangeSets.values.sumBy { it.size }

    override fun add(change: ModelChange) = _modelChangeSets
        .getOrPut(change.targetModel) { ModelSpecificAdditiveChangeSet(change.targetModel) }
        .add(change)

    override fun addAll(changes: Collection<ModelChange>) =
        changes.fold(false) { lastResult, change -> add(change) || lastResult }

    override fun addAll(changes: ChangeSet): Boolean =
        when (changes) {
            is ModelSpecificAdditiveChangeSet -> addAll(changes)
            is DefaultAdditiveChangeSet -> changes._modelChangeSets.values
                .fold(false) { lastResult, modelSpecificChangeSet ->
                    addAll(modelSpecificChangeSet) || lastResult
                }
            else -> addAll(changes as Collection<ModelChange>)
        }

    private fun addAll(modelSpecificChangeSet: ModelSpecificAdditiveChangeSet): Boolean {
        val currentChangeSet = _modelChangeSets[modelSpecificChangeSet.targetModel]
        return if (currentChangeSet == null) {
            _modelChangeSets[modelSpecificChangeSet.targetModel] = modelSpecificChangeSet.copy()
            true
        } else {
            currentChangeSet.addAll(modelSpecificChangeSet)
        }
    }

    override fun filterByModel(modelIdentity: ModelIdentity) =
        _modelChangeSets.getOrElse(modelIdentity) { ChangeSet.EMPTY }

    override fun contains(element: ModelChange) = _modelChangeSets[element.targetModel]?.contains(element) == true

    override fun containsAll(elements: Collection<ModelChange>) = elements.all { contains(it) }

    override fun isEmpty() = _modelChangeSets.values.all { it.isEmpty() }

    override fun copy() = DefaultAdditiveChangeSet(HashMap(this._modelChangeSets))

    override fun iterator() = FlatMapIterator(_modelChangeSets.values.iterator())
    override fun equals(other: Any?) = standardEquals(other)
    override fun hashCode() = standardHashCode()
    override fun toString() = standardToString()
}

fun changeSetOf(vararg changes: ModelChange): ChangeSet =
    DefaultAdditiveChangeSet().also { it.addAll(changes.toList()) }

fun changeSetOf(vararg changes: ChangeSet): ChangeSet =
    DefaultAdditiveChangeSet().also { changes.forEach { set -> it.addAll(set) } }

operator fun ChangeSet.plus(other: ChangeSet): AdditiveChangeSet {
    val result =
        if (this is DefaultAdditiveChangeSet) this.copy()
        else DefaultAdditiveChangeSet().also { it += this }
    result += other
    return result
}