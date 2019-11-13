package de.joshuagleitze.transformationnetwork.simulator.util.geometry

import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan


class Angle constructor(val radians: Double) : Comparable<Angle> {
    constructor(radians: Number) : this(radians.toDouble())

    val degrees get() = radians * DEGREES_IN_RADIAN

    fun sine() = sin(radians)
    fun cosine() = cos(radians)
    fun tangent() = tan(radians)

    operator fun unaryMinus() = Angle(-radians)

    override fun compareTo(other: Angle) = (radians.normalize() - other.radians.normalize()).let { difference ->
        when {
            difference < .0 -> -1
            difference == .0 -> 0
            else -> 1
        }
    }

    private fun Double.normalize() = this.fMod(MAX_RADIAN)
    operator fun times(factor: Byte) = Angle(radians * factor)
    operator fun times(factor: Short) = Angle(radians * factor)
    operator fun times(factor: Int) = Angle(radians * factor)
    operator fun times(factor: Long) = Angle(radians * factor)
    operator fun times(factor: Float) = Angle(radians * factor)
    operator fun times(factor: Double) = Angle(radians * factor)

    operator fun div(divident: Byte) = Angle(radians / divident)
    operator fun div(divident: Short) = Angle(radians / divident)
    operator fun div(divident: Int) = Angle(radians / divident)
    operator fun div(divident: Long) = Angle(radians / divident)
    operator fun div(divident: Float) = Angle(radians / divident)
    operator fun div(divident: Double) = Angle(radians / divident)
    operator fun plus(angle: Angle) = Angle(radians + angle.radians)
    operator fun minus(angle: Angle) = Angle(radians - angle.radians)

    companion object {
        fun between(a: Coordinate, b: Coordinate) = Angle(atan2(-b.y + a.y, b.x - a.x))
        val PI = Angle(kotlin.math.PI)

        fun arcsine(x: Double) = Angle(asin(x))
        fun arccosine(x: Double) = Angle(acos(x))
        fun arctangent(x: Double) = Angle(atan(x))

        private const val MAX_RADIAN = 2 * kotlin.math.PI
        private const val DEGREES_IN_RADIAN = 180.0 / kotlin.math.PI
    }

    private fun Double.fMod(other: Double) = ((this % other) + other) % other

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false
        other as Angle

        return radians.normalize() == other.radians.normalize()
    }

    override fun hashCode(): Int {
        return radians.normalize().hashCode()
    }

    override fun toString(): String {
        val pis = radians / kotlin.math.PI
        return "Angle(radians=$pis*π)"
    }
}

operator fun Byte.times(angle: Angle) = angle * this
operator fun Short.times(angle: Angle) = angle * this
operator fun Int.times(angle: Angle) = angle * this
operator fun Long.times(angle: Angle) = angle * this
operator fun Float.times(angle: Angle) = angle * this
operator fun Double.times(angle: Angle) = angle * this
