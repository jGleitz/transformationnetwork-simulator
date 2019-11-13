package de.joshuagleitze.transformationnetwork.changemetamodel

interface ChangeSet : Set<ModelChange> {
    val deletions: Collection<DeletionChange>
    val additions: Collection<AdditionChange>
    val modifications: Collection<AttributeChange>

    fun copy(): ChangeSet

    companion object {
        val EMPTY = object : ChangeSet {
            override val deletions: Collection<DeletionChange> get() = emptySet()
            override val additions: Collection<AdditionChange> get() = emptySet()
            override val modifications: Collection<AttributeChange> get() = emptySet()
            override val size: Int get() = 0

            override fun contains(element: ModelChange) = false
            override fun containsAll(elements: Collection<ModelChange>) = false
            override fun isEmpty() = true
            override fun iterator() = emptySet<ModelChange>().iterator()
            override fun copy() = this
        }
    }
}