package de.joshuagleitze.transformationnetwork.metamodelling

interface Metaclass {
    val name: String
    val attributes: Set<MetaAttribute<*>>
}