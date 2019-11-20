package de.joshuagleitze.transformationnetwork.changemetamodel.changeset

import de.joshuagleitze.transformationnetwork.changemetamodel.AdditionChange
import de.joshuagleitze.transformationnetwork.changemetamodel.AttributeChange
import de.joshuagleitze.transformationnetwork.changemetamodel.DeletionChange
import de.joshuagleitze.transformationnetwork.changemetamodel.ModelChange
import de.joshuagleitze.transformationnetwork.changemetamodel.ModelObjectChange
import de.joshuagleitze.transformationnetwork.changemetamodel.util.FlatMapIterator
import de.joshuagleitze.transformationnetwork.metametamodel.ModelIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObject

class DefaultAdditiveChangeSet private constructor(
    private val _modelChangeSets: MutableMap<ModelIdentity, ModelSpecificAdditiveChangeSet>,
    private val _objectChangeSets: MutableMap<ModelObject, ObjectSpecificAdditiveChangeSet>
) : AdditiveChangeSet {
    constructor() : this(HashMap(), HashMap())

    override val affectedModels: Set<ModelIdentity>
        get() = HashSet<ModelIdentity>().also { result ->
            _modelChangeSets.values.flatMapTo(result) { it.affectedModels }
            _objectChangeSets.values.flatMapTo(result) { it.affectedModels }
        }
    override val additions: Collection<AdditionChange> get() = _modelChangeSets.values.flatMap { it.additions }
    override val deletions: Collection<DeletionChange> get() = _modelChangeSets.values.flatMap { it.deletions }
    override val modifications: Collection<AttributeChange> get() = _objectChangeSets.values.flatMap { it.modifications }
    override val size: Int get() = _modelChangeSets.values.sumBy { it.size }

    override fun add(change: ModelChange) = when (change) {
        is ModelObjectChange -> _modelChangeSets
            .getOrPut(change.targetModel) { ModelSpecificAdditiveChangeSet(change.targetModel) }
            .add(change)
        is AttributeChange -> _objectChangeSets
            .getOrPut(change.targetObject) { ObjectSpecificAdditiveChangeSet(change.targetObject) }
            .add(change)
    }

    override fun addAll(changes: Collection<ModelChange>) =
        changes.fold(false) { lastResult, change -> add(change) || lastResult }

    override fun addAll(changes: ChangeSet): Boolean =
        when (changes) {
            is ModelSpecificAdditiveChangeSet -> addAll(changes)
            is ObjectSpecificAdditiveChangeSet -> addAll(changes)
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

    private fun addAll(objectSpecificChangeSet: ObjectSpecificAdditiveChangeSet): Boolean {
        val currentChangeSet = _objectChangeSets[objectSpecificChangeSet.targetObject]
        return if (currentChangeSet == null) {
            _objectChangeSets[objectSpecificChangeSet.targetObject] = objectSpecificChangeSet.copy()
            true
        } else {
            currentChangeSet.addAll(objectSpecificChangeSet)
        }
    }

    override fun filterByModel(modelIdentity: ModelIdentity) =
        _modelChangeSets.getOrElse(modelIdentity) { ChangeSet.EMPTY }

    override fun contains(element: ModelChange) = _modelChangeSets[element.targetModel]?.contains(element) == true

    override fun containsAll(elements: Collection<ModelChange>) = elements.all { contains(it) }

    override fun isEmpty() =
        _modelChangeSets.values.all { it.isEmpty() } && _objectChangeSets.values.all { it.isEmpty() }

    override fun copy() =
        DefaultAdditiveChangeSet(HashMap(this._modelChangeSets), HashMap(this._objectChangeSets))

    override fun iterator() = FlatMapIterator(_modelChangeSets.values.iterator())

    override fun equals(other: Any?) = standardEquals(other)
    override fun hashCode() = standardHashCode()
    override fun toString() = standardToString()
}

operator fun ChangeSet.plus(other: ChangeSet): DefaultAdditiveChangeSet {
    val result =
        if (this is DefaultAdditiveChangeSet) this.copy()
        else DefaultAdditiveChangeSet().also { it += this }
    result += other
    return result
}