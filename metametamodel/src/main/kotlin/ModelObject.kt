package de.joshuagleitze.transformationnetwork.metametamodel

interface ModelObject {
    val metaclass: Metaclass
    val attributes: MetaAttributeMap
    val model: Model?

    operator fun <T : Any> get(attribute: MetaAttribute<T>) = attributes[attribute]

    operator fun <T : Any> set(attribute: MetaAttribute<T>, value: T?) = attributes.set(attribute, value)
    operator fun set(attribute: MetaAttribute<*>, value: Any?) = attributes.set(attribute, value)

    fun copy(): ModelObject
}