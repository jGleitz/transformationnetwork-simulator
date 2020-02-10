package de.joshuagleitze.transformationnetwork.changemetamodel.util

class JoinedIterator<T>(private val firstIterator: Iterator<T>, private val secondIterator: Iterator<T>) : Iterator<T> {
    override fun hasNext() = firstIterator.hasNext() || secondIterator.hasNext()

    override fun next() = when {
        firstIterator.hasNext() -> firstIterator
        else -> secondIterator
    }.next()
}

operator fun <T> Iterator<T>.plus(next: Iterator<T>): Iterator<T> = JoinedIterator(this, next)