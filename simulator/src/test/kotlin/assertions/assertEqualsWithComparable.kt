package de.joshuagleitze.transformationnetwork.simulator.assertions

import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun <T : Comparable<T>> assertEqualsWithComparable(a: T, b: T) {
    assertEquals(a, b)
    assertTrue(a.compareTo(b) == 0)
}