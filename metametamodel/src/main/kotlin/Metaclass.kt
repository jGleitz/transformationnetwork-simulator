package de.joshuagleitze.transformationnetwork.metametamodel

interface Metaclass {
    val name: String
    val attributes: Set<MetaAttribute<*>>
    val ownAttributes: Set<MetaAttribute<*>>
    val superClasses: Set<Metaclass>
    fun createNew(identity: ModelObjectIdentity? = null): ModelObject
}