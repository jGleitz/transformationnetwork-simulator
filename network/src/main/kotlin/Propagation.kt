package de.joshuagleitze.transformationnetwork.network

interface Propagation {
    fun isFinished(): Boolean
    fun propagateNext()
}