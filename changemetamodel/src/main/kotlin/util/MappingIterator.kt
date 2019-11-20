package de.joshuagleitze.transformationnetwork.changemetamodel.util

class MappingIterator<S, T>(private val sourceIterator: Iterator<S>, private val mapping: (S) -> T) : Iterator<T> {
    override fun hasNext() = sourceIterator.hasNext()

    override fun next() = mapping(sourceIterator.next())
}