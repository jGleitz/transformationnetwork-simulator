package de.joshuagleitze.transformationnetwork.simulator.assertions

import kotlin.test.assertTrue

fun <T : Comparable<T>> assertFirstLess(a: T, b: T) {
    assertTrue(a < b, "expected $a to be less than $b!")
}