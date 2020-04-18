package de.joshuagleitze.transformationnetwork.metametamodel

typealias AnyMetaclass = Metaclass<*>

interface Metaclass<O : ModelObject<O>> {
    val name: String
    val attributes: Set<AnyMetaAttribute>
    val ownAttributes: Set<AnyMetaAttribute>
    val superClasses: Set<AnyMetaclass>

    fun createNew(identity: AnyModelObjectIdentity? = null): O
}
