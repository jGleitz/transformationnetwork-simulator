package de.joshuagleitze.transformationnetwork.transformations.busybeaver3

import kotlin.math.max
import kotlin.math.min

internal inline infix fun Boolean.implies(expression: () -> Boolean) = !this || expression()
internal fun String.findFirstRight(char: Char, from: Int) = this.indexOf(char, from).nullIfNotFound()
internal fun String.findFirstLeft(char: Char, from: Int) = this.lastIndexOf(char, from).nullIfNotFound()
internal fun minToMax(a: Int, b: Int) = min(a, b)..max(a, b)

private fun Int.nullIfNotFound() = if (this == -1) null else this
