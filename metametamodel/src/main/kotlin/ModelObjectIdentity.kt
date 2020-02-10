package de.joshuagleitze.transformationnetwork.metametamodel

interface ModelObjectIdentity : Identity<ModelObject> {
    val metaclass: Metaclass
}