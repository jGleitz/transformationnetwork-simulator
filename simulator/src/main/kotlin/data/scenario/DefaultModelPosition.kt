package de.joshuagleitze.transformationnetwork.simulator.data.scenario

private data class DefaultModelPosition(override val column: Int, override val row: Int) : ModelPosition

infix fun Int.x(y: Int): ModelPosition = DefaultModelPosition(this, y)
