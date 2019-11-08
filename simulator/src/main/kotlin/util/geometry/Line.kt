package de.joshuagleitze.transformationnetwork.simulator

data class Line(val start: Coordinate, val end: Coordinate) {
    operator fun plus(coordinate: Coordinate) = Line(start + coordinate, end + coordinate)
    operator fun minus(coordinate: Coordinate) = Line(start - coordinate, end - coordinate)

    val length get() = start.distanceTo(end)
}