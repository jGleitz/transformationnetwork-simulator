package de.joshuagleitze.transformationnetwork.simulator.util.svg.elementdsl

import kotlinx.html.HTMLTag
import kotlinx.html.TagConsumer
import kotlinx.html.attributesMapOf
import react.RBuilder
import react.dom.tag


fun RBuilder.use(href: String, transform: String? = null) =
    tag({}) { USE(attributesMapOf("href", href, "transform", transform), it) }

class USE(initialAttributes: Map<String, String>, override val consumer: TagConsumer<*>) :
    HTMLTag("use", consumer, initialAttributes, null, true, true)