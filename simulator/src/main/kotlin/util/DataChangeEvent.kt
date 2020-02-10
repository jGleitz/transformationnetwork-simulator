package de.joshuagleitze.transformationnetwork.simulator.util

import kotlinext.js.jsObject
import org.w3c.dom.events.Event

object DataChangeEvent {
    const val name = "datachange"

    operator fun invoke() = Event(name, jsObject { bubbles = true })
}
