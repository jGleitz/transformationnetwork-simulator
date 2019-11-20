package de.joshuagleitze.transformationnetwork.metametamodel

interface ModelIdentity {
    val metamodel: Metamodel
    fun identifies(model: Model): Boolean
}