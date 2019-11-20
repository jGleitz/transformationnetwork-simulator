package de.joshuagleitze.transformationnetwork.changemetamodel.util

class FlatMapIterator<T>(private val outerIterator: Iterator<Iterable<T>>) : Iterator<T> {
    private var currentIterator: Iterator<T>? = null

    override fun hasNext() = findNextIterator()?.hasNext() == true || outerIterator.hasNext()

    override fun next(): T =
        findNextIterator().let { currentIterator ->
            if (currentIterator == null || !currentIterator.hasNext()) {
                throw NoSuchElementException()
            } else currentIterator
        }.next()

    private fun findNextIterator(): Iterator<T>? {
        var iterator = currentIterator
        while ((iterator == null || !iterator.hasNext()) && outerIterator.hasNext()) {
            iterator = outerIterator.next().iterator()
        }
        currentIterator = iterator
        return iterator
    }
}