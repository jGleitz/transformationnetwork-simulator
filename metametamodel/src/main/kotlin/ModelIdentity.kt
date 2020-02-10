package de.joshuagleitze.transformationnetwork.metametamodel

interface ModelIdentity: Identity<Model> {
    val metamodel: Metamodel
}