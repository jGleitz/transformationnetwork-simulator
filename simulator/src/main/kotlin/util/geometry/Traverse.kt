package de.joshuagleitze.transformationnetwork.simulator.util.geometry

data class Traverse(val coordinates: List<Coordinate>) {
    constructor(vararg coordinates: Coordinate) : this(coordinates.asList())

    init {
        check(coordinates.size >= 2) { "A traverse needs at least 2 coordinates!" }
    }

    val start = coordinates.first()
    val end = coordinates.last()

    operator fun plus(coordinate: Coordinate) = transformCoordinates { it + coordinate }
    operator fun minus(coordinate: Coordinate) = transformCoordinates { it - coordinate }

    fun transformCoordinates(transformation: (Coordinate) -> Coordinate) =
        Traverse(coordinates.map(transformation))

    val length: Double
        get() = coordinates
            .windowed(2)
            .fold(.0) { length, (first, second) -> length + first.distanceTo(second) }
}
