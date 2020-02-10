package de.joshuagleitze.transformationnetwork.metametamodel

interface Identity<Identified> {
    val identifyingString: String
    fun identifies(candidate: Identified): Boolean
}