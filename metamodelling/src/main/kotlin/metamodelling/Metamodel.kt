package de.joshuagleitze.transformationnetwork.metamodelling

interface Metamodel {
    val name: String
    val classes: Set<Metaclass>
}