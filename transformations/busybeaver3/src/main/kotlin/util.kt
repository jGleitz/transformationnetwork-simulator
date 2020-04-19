package de.joshuagleitze.transformationnetwork.transformations.busybeaver3

internal inline infix fun Boolean.implies(expression: () -> Boolean) = !this || expression()
internal fun String.findFirstRight(char: Char, from: Int) = this.indexOf(char, from).nullIfNotFound()
internal fun String.findFirstLeft(char: Char, from: Int) = this.lastIndexOf(char, from).nullIfNotFound()
internal fun String.set(range: IntRange, char: Char) = this.replaceRange(range, "$char".repeat(range.count()))
internal fun String.set(position: Int, char: Char) = this.set(position..position, char)
internal inline fun <T> T.runIf(predicate: T.() -> Boolean, f: T.() -> T) = if (this.predicate()) this.f() else this
private fun Int.nullIfNotFound() = if (this == -1) null else this
