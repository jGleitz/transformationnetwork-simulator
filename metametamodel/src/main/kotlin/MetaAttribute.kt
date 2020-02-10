package de.joshuagleitze.transformationnetwork.metametamodel

interface MetaAttribute<T : Any> {
    val name: String

    fun canBeValue(value: Any?): Boolean
    fun checkCanBeValue(value: Any?)
}