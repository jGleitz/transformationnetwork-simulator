package de.joshuagleitze.transformationnetwork.metametamodel

typealias AnyModelObjectIdentity = ModelObjectIdentity<*>

interface ModelObjectIdentity<O : ModelObject<O>> : Identity<ModelObject<*>> {
    val metaclass: Metaclass<O>
}
