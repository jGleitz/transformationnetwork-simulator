package de.joshuagleitze.transformationnetwork.metamodelling

interface Model {
    val metamodel: Metamodel
    val name: String
    val objects: List<ModelObject>
}