package de.joshuagleitze.transformationnetwork.simulator.svgdsl

import kotlinx.html.HTMLTag
import kotlinx.html.TagConsumer
import kotlinx.html.attributesMapOf
import react.RBuilder
import react.dom.RDOMBuilder
import react.dom.tag

fun RBuilder.filter(id: String, block: RDOMBuilder<FILTER>.() -> Unit) =
    tag(block) { FILTER(attributesMapOf("id", id), it) }

class FILTER(initialAttributes: Map<String, String>, override val consumer: TagConsumer<*>) :
    HTMLTag("filter", consumer, initialAttributes, null, false, false)
