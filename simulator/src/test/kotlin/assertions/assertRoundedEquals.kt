package de.joshuagleitze.transformationnetwork.simulator.assertions

import de.joshuagleitze.transformationnetwork.simulator.util.geometry.Coordinate
import kotlin.math.abs
import kotlin.math.pow
import kotlin.test.assertTrue

private val δ = 2.0.pow(-16)

fun assertRoundedEquals(expected: Double, actual: Double, message: String? = null) {
    assertTrue(
        abs(actual - expected) < δ,
        (message?.let { "$it " } ?: "") + "expected $expected, actual $actual"
    )
}

fun assertRoundedEquals(expected: Coordinate, actual: Coordinate, message: String? = null) {
    assertRoundedEquals(expected.x, actual.x, (message?.let { "$it – " } ?: "") + "x:")
    assertRoundedEquals(expected.y, actual.y, (message?.let { "$it – " } ?: "") + "y:")
}