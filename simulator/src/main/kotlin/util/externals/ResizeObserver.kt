package de.joshuagleitze.transformationnetwork.simulator.util.externals

import kotlinext.js.jsObject
import org.w3c.dom.DOMRectReadOnly
import org.w3c.dom.Element

external interface BoxSize {
    val blockSize: Double
    val inlineSize: Double
}

external interface ObservationOptions {
    val box: String
}

external interface ResizeObserverEntry {
    val borderBoxSize: BoxSize
    val contentBoxSize: BoxSize
    val contentRect: DOMRectReadOnly
    val target: Element
}

@JsName("ResizeObserver")
external class ResizeObserver(callback: (entries: Array<ResizeObserverEntry>, observer: ResizeObserver) -> Unit) {
    fun observe(element: Element, options: ObservationOptions = definedExternally)
    fun unobserve(element: Element)
    fun disconnect()
}