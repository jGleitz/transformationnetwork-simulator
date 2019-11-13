package de.joshuagleitze.transformationnetwork.simulator.util.svg.elementdsl

import kotlinx.html.HTMLTag
import kotlinx.html.TagConsumer
import kotlinx.html.attributesMapOf
import react.RBuilder
import react.dom.RDOMBuilder
import react.dom.tag

fun RBuilder.marker(
    id: String,
    markerWidth: String,
    markerHeight: String,
    refX: String? = null,
    refY: String? = null,
    orient: String,
    markerUnits: String? = null,
    viewBox: String? = null,
    block: RDOMBuilder<MARKER>.() -> Unit = {}
) =
    tag(block) {
        MARKER(
            attributesMapOf(
                "id", id,
                "markerWidth", markerWidth,
                "markerHeight", markerHeight,
                "refX", refX,
                "refY", refY,
                "orient", orient,
                "viewBox", viewBox,
                "markerUnits", markerUnits
            ), it
        )
    }

class MARKER(initialAttributes: Map<String, String>, override val consumer: TagConsumer<*>) :
    HTMLTag("marker", consumer, initialAttributes, null, false, false) {
}

