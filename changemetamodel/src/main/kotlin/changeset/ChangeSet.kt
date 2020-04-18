package de.joshuagleitze.transformationnetwork.changemetamodel.changeset

import de.joshuagleitze.transformationnetwork.changemetamodel.AdditionChange
import de.joshuagleitze.transformationnetwork.changemetamodel.AttributeChange
import de.joshuagleitze.transformationnetwork.changemetamodel.DeletionChange
import de.joshuagleitze.transformationnetwork.changemetamodel.ModelChange
import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.metametamodel.ModelIdentity

interface ChangeSet : Set<ModelChange> {
    val deletions: Collection<DeletionChange<*>>
    val additions: Collection<AdditionChange<*>>
    val modifications: Collection<AttributeChange<*>>
    val affectedModels: Set<ModelIdentity>

    fun copy(): ChangeSet
    fun filterByModel(model: Model) = filterByModel(model.identity)
    fun filterByModel(modelIdentity: ModelIdentity): ChangeSet

    companion object {
        val EMPTY = object : ChangeSet {
            override val affectedModels: Set<ModelIdentity> get() = emptySet()
            override val deletions: Collection<DeletionChange<*>> get() = emptySet()
            override val additions: Collection<AdditionChange<*>> get() = emptySet()
            override val modifications: Collection<AttributeChange<*>> get() = emptySet()
            override val size: Int get() = 0

            override fun contains(element: ModelChange) = false
            override fun containsAll(elements: Collection<ModelChange>) = false
            override fun isEmpty() = true
            override fun filterByModel(modelIdentity: ModelIdentity) = this
            override fun iterator() = emptySet<ModelChange>().iterator()
            override fun copy() = this
        }
    }
}
