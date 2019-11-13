package de.joshuagleitze.transformationnetwork.simulator.util.geometry

data class Line(val start: Coordinate, val end: Coordinate) {
    operator fun plus(coordinate: Coordinate) = transformCoordinates { it + coordinate }
    operator fun minus(coordinate: Coordinate) = transformCoordinates { it - coordinate }

    fun transformCoordinates(transformation: (Coordinate) -> Coordinate) =
        Line(transformation(start), transformation(end))

    val length get() = start.distanceTo(end)
}