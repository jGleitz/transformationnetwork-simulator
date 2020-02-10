package de.joshuagleitze.transformationnetwork.simulator.util

import kotlinext.js.jsObject
import org.w3c.dom.events.Event

class DataChangeEvent : Event(name, jsObject { bubbles = true }) {
    companion object {
        val name = "dataChange"
    }
}
