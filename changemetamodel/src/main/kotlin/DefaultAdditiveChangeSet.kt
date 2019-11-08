package de.joshuagleitze.transformationnetwork.changemetamodel

import de.joshuagleitze.transformationnetwork.metametamodel.MetaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObject

class DefaultAdditiveChangeSet(override val targetModel: Model) : AdditiveChangeSet {
    private val _additions = HashMap<ModelObject, AdditionChange>()
    override val additions: Collection<AdditionChange> get() = _additions.values
    private val _deletions = HashMap<ModelObject, DeletionChange>()
    override val deletions: Collection<DeletionChange> get() = _deletions.values
    private val _modifications = HashMap<ModelObject, HashMap<MetaAttribute<*>, AttributeChange>>()
    override val modifications: Collection<AttributeChange> get() = _modifications.values.flatMap { it.values }

    override fun add(change: ModelChange): Boolean {
        check(change.targetModel == targetModel) { "The change '$change' does not target this change set’s target model '$targetModel', but '${change.targetModel}'!" }
        return when (change) {
            is AdditionChange -> addAdditionChange(change)
            is DeletionChange -> addDeletionChange(change)
            is AttributeChange -> addAttributeChange(change)
        }
    }

    private fun addAdditionChange(change: AdditionChange): Boolean {
        if (!_deletions.containsKey(change.addedObject)) {
            _modifications -= change.addedObject
            return _additions.put(change.addedObject, change) == null
        }
        return false
    }

    private fun addDeletionChange(change: DeletionChange): Boolean {
        _additions -= change.deletedObject
        _modifications -= change.deletedObject
        return _deletions.put(change.deletedObject, change) == null
    }

    private fun addAttributeChange(change: AttributeChange): Boolean {
        if (!_deletions.containsKey(change.targetObject) && !_additions.containsKey(change.targetObject)) {
            return _modifications.getOrPut(change.targetObject) { HashMap() }
                .put(change.targetAttribute, change) == null
        }
        return false
    }

    override fun addAll(changes: Collection<ModelChange>) =
        changes.fold(false) { lastResult, change -> add(change) || lastResult }

    override val size: Int get() = additions.size + deletions.size + modifications.size

    override fun contains(element: ModelChange) = when (element) {
        is AdditionChange -> additions.contains(element)
        is DeletionChange -> deletions.contains(element)
        is AttributeChange -> modifications.contains(element)
    }

    override fun containsAll(elements: Collection<ModelChange>) = elements.all { contains(it) }

    override fun isEmpty() = additions.isEmpty() && deletions.isEmpty() && modifications.isEmpty()

    override fun iterator(): Iterator<ModelChange> {
        val addedIterator = additions.iterator()
        val deletedIterator = deletions.iterator()
        val modifiedIterator = modifications.iterator()
        return object : Iterator<ModelChange> {
            override fun hasNext() = addedIterator.hasNext() || deletedIterator.hasNext() || modifiedIterator.hasNext()

            override fun next() = when {
                addedIterator.hasNext() -> addedIterator
                deletedIterator.hasNext() -> deletedIterator
                else -> modifiedIterator
            }.next()
        }
    }
}