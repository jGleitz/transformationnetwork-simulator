package de.joshuagleitze.transformationnetwork.model

import model.MetaAttributeMap

interface ModelObject {
    val metaclass: Metaclass
    val attributes: MetaAttributeMap
}