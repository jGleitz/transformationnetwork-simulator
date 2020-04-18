package de.joshuagleitze.transformationnetwork.metametamodel

typealias AnyModelObject = ModelObject<*>

interface ModelObject<O : ModelObject<O>> {
    val metaclass: Metaclass<O>
    val attributes: MetaAttributeMap
    val model: Model?
    val identity: ModelObjectIdentity<O>

    operator fun <T : Any> get(attribute: MetaAttribute<T>) = attributes[attribute]

    operator fun <T : Any> set(attribute: MetaAttribute<T>, value: T?) = attributes.set(attribute, value)
    operator fun set(attribute: MetaAttribute<*>, value: Any?) = attributes.set(attribute, value)

    fun copy(): O
}
