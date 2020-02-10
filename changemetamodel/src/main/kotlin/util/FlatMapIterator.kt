package de.joshuagleitze.transformationnetwork.changemetamodel.util

class FlatMapIterator<T>(private val outerIterator: Iterator<Iterable<T>>) : Iterator<T> {
    private var currentIterator: Iterator<T>? = null

    override fun hasNext() = findNextIterator()?.hasNext() == true

    override fun next(): T = findNextIterator()?.next() ?: throw NoSuchElementException()

    private fun findNextIterator(): Iterator<T>? {
        var iterator = currentIterator
        while ((iterator == null || !iterator.hasNext()) && outerIterator.hasNext()) {
            iterator = outerIterator.next().iterator()
        }
        currentIterator = iterator
        return iterator
    }
}