package de.joshuagleitze.transformationnetwork.metametamodel

typealias AnyMetaAttribute = MetaAttribute<*>

interface MetaAttribute<T : Any> {
    val name: String

    fun canBeValue(value: Any?): Boolean
    fun checkCanBeValue(value: Any?)
}
