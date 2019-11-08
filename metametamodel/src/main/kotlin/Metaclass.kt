package de.joshuagleitze.transformationnetwork.metametamodel

interface Metaclass {
    val name: String
    val attributes: Set<MetaAttribute<*>>
}