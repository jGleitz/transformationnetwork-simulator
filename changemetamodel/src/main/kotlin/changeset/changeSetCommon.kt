package de.joshuagleitze.transformationnetwork.changemetamodel.changeset


internal fun ChangeSet.standardToString() =
    if (this.isEmpty()) "[[[ ]]]"
    else this.joinToString(prefix = "[[[\n\t", postfix = "\n]]]", separator = ",\n\t")

internal fun ChangeSet.standardEquals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || other !is ChangeSet) return false

    return this.size == other.size && this.containsAll(other)
}

internal fun ChangeSet.standardHashCode() = this.sumBy { it.hashCode() }