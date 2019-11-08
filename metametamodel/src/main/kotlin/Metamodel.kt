package de.joshuagleitze.transformationnetwork.metametamodel

interface Metamodel {
    val name: String
    val classes: Set<Metaclass>
}