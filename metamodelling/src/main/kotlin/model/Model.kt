package de.joshuagleitze.transformationnetwork.model

interface Model {
    val metamodel: Metamodel
    val objects: List<ModelObject>
}