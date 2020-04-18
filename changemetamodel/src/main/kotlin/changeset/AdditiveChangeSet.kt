package de.joshuagleitze.transformationnetwork.changemetamodel.changeset

import de.joshuagleitze.transformationnetwork.changemetamodel.ModelChange

interface AdditiveChangeSet :
    ChangeSet {
    fun add(change: ModelChange): Boolean
    fun addAll(changes: Collection<ModelChange>): Boolean
    fun addAll(changes: ChangeSet): Boolean

    operator fun plusAssign(change: ModelChange) {
        this.add(change)
    }

    operator fun plusAssign(changes: ChangeSet) {
        this.addAll(changes)
    }

    override fun copy(): AdditiveChangeSet
}
