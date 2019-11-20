package de.joshuagleitze.transformationnetwork.metametamodel

interface MetaAttributeMap {
    operator fun <T : Any> get(attribute: MetaAttribute<T>): T?
    operator fun <T : Any> set(attribute: MetaAttribute<T>, value: T?)
    operator fun set(attribute: MetaAttribute<*>, value: Any?)
}