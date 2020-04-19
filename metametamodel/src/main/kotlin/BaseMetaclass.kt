package de.joshuagleitze.transformationnetwork.metametamodel

abstract class BaseMetaclass<O : ModelObject<O>> : Metaclass<O> {
    final override val attributes: Set<MetaAttribute<*>>
        get() {
            val attributesByName = LinkedHashMap<String, MetaAttribute<*>>()
            attributesByName += ownAttributes.associateBy { it.name }
            superClasses.flatMap { it.attributes }.forEach {
                attributesByName.getOrPut(it.name) { it }
            }
            return attributesByName.values.toSet()
        }

    override fun toString() = name
}
