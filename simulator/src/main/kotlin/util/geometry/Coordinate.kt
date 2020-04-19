package de.joshuagleitze.transformationnetwork.simulator.util.geometry

import kotlin.math.ceil
import kotlin.math.floor
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
    fun distanceTo(other: Coordinate) = (this - other).let { (x, y) -> sqrt(x * x + y * y) }
    fun normalize() = this / this.distanceTo(Zero)
    operator fun unaryMinus() = componentWise { -it }

    override fun toString() = "($x,$y)"

    fun isInteger() = x.isInteger() && y.isInteger()

    private fun Double.isInteger() = this.isFinite() && floor(this) == ceil(this)

    companion object {
        val Zero = Coordinate(0, 0)
        fun fromPolar(theta: Angle, r: Double) = Coordinate(r * theta.cosine(), r * theta.sine())
    }
}

operator fun Byte.times(coordinate: Coordinate) = coordinate * this
operator fun Short.times(coordinate: Coordinate) = coordinate * this
operator fun Int.times(coordinate: Coordinate) = coordinate * this
operator fun Long.times(coordinate: Coordinate) = coordinate * this
operator fun Float.times(coordinate: Coordinate) = coordinate * this
operator fun Double.times(coordinate: Coordinate) = coordinate * this
infix fun Double.x(y: Double) = Coordinate(this, y)
infix fun Number.x(y: Number) = Coordinate(this, y)
