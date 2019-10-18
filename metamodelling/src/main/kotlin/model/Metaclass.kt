package de.joshuagleitze.transformationnetwork.model

interface Metaclass {
    val name: String
    val attributes: Set<MetaAttribute<*>>
}