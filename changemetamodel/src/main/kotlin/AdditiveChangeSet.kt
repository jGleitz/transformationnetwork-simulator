package de.joshuagleitze.transformationnetwork.changemetamodel

interface AdditiveChangeSet : ChangeSet {
    fun add(change: ModelChange): Boolean
    fun addAll(changes: Collection<ModelChange>): Boolean

    operator fun plusAssign(change: ModelChange) {
        this.add(change)
    }
}