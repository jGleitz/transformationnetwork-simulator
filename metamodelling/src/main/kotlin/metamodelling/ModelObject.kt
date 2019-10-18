package de.joshuagleitze.transformationnetwork.metamodelling

interface ModelObject {
    val metaclass: Metaclass
    val attributes: MetaAttributeMap
}