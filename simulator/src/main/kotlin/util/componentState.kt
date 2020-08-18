package de.joshuagleitze.transformationnetwork.simulator.util

import react.RComponent

@Suppress("NOTHING_TO_INLINE")
inline fun <T : Any> RComponent<*, *>.checkAvailable(data: T?) =
    checkNotNull(data) { "${this::class.simpleName} has not mounted yet!" }

inline fun <T : Any, R> RComponent<*, *>.checkAvailable(data: T?, block: (T) -> R) =
    checkNotNull(data) { "${this::class.simpleName} has not mounted yet!" }.let(block)
