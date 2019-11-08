package de.joshuagleitze.transformationnetwork.simulator

import kotlin.math.sqrt

data class Coordinate(val x: Double, val y: Double) {
    constructor(x: Number, y: Number) : this(x.toDouble(), y.toDouble())

    operator fun minus(other: Coordinate) = Coordinate(x - other.x, y - other.y)

    operator fun plus(other: Coordinate) = Coordinate(x + other.x, y + other.y)

    operator fun times(factor: Byte) = componentWise { it * factor }
    operator fun times(factor: Short) = componentWise { it * factor }
    operator fun times(factor: Int) = componentWise { it * factor }
    operator fun times(factor: Long) = componentWise { it * factor }
    operator fun times(factor: Float) = componentWise { it * factor }
    operator fun times(factor: Double) = componentWise { it * factor }

    operator fun div(divident: Byte) = componentWise { it / divident }
    operator fun div(divident: Short) = componentWise { it / divident }
    operator fun div(divident: Int) = componentWise { it / divident }
    operator fun div(divident: Long) = componentWise { it / divident }
    operator fun div(divident: Float) = componentWise { it / divident }
    operator fun div(divident: Double) = componentWise { it / divident }

    inline fun componentWise(operation: (Double) -> Double) = Coordinate(operation(x), operation(y))
    fun distanceTo(other: Coordinate) = (this - other).componentWise { it * it }.let { (x, y) -> sqrt(x + y) }
    fun normalize() = this / this.distanceTo(Zero)

    companion object {
        val Zero = Coordinate(0, 0)
    }
}

operator fun Byte.times(angle: Angle) = Angle(this * angle.radians)
operator fun Short.times(angle: Angle) = Angle(this * angle.radians)
operator fun Int.times(angle: Angle) = Angle(this * angle.radians)
operator fun Long.times(angle: Angle) = Angle(this * angle.radians)
operator fun Float.times(angle: Angle) = Angle(this * angle.radians)
operator fun Double.times(angle: Angle) = Angle(this * angle.radians)