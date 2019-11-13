package de.joshuagleitze.transformationnetwork.simulator.util.svg.elementdsl

import kotlinx.html.HTMLTag
import kotlinx.html.TagConsumer
import kotlinx.html.attributesMapOf
import react.RBuilder
import react.dom.RDOMBuilder
import react.dom.tag

fun RBuilder.defs(block: RDOMBuilder<DEFS>.() -> Unit) = tag(block) { DEFS(attributesMapOf(), it) }
class DEFS(initialAttributes: Map<String, String>, override val consumer: TagConsumer<*>) :
    HTMLTag("defs", consumer, initialAttributes, null, false, false)
