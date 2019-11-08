package de.joshuagleitze.transformationnetwork.simulator

import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan


inline class Angle constructor(val radians: Double) : Comparable<Angle> {
    val degrees get() = radians * DEGREES_IN_RADIAN

    fun sine() = sin(radians)

    fun arcsine() = asin(radians)

    fun cosine() = cos(radians)
    fun arccosine() = acos(radians)
    fun tangent() = tan(radians)
    fun arctangent() = atan(radians)
    operator fun rangeTo(endInclusive: Angle) = AngleRange(this, endInclusive)
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
        fun between(a: Coordinate, b: Coordinate) = Angle(atan2(a.y - b.y, a.x - b.x))
        val PI = Angle(kotlin.math.PI)

        private const val MAX_RADIAN = 2 * kotlin.math.PI
        private const val DEGREES_IN_RADIAN = 180.0 / kotlin.math.PI
    }

    class AngleRange(
        override val start: Angle,
        override val endInclusive: Angle
    ) : ClosedRange<Angle>

    private fun Double.fMod(other: Double) = ((this % other) + other) % other
}
