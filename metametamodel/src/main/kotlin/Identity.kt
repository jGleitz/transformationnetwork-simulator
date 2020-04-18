package de.joshuagleitze.transformationnetwork.metametamodel

interface Identity<in Identified> {
    val identifyingString: String
    fun identifies(candidate: Identified): Boolean
}
