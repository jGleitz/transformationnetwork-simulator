package de.joshuagleitze.transformationnetwork.simulator.svgdsl

import kotlinx.html.HTMLTag
import kotlinx.html.TagConsumer
import kotlinx.html.attributesMapOf
import react.RBuilder
import react.dom.tag
import styled.StyledDOMBuilder
import styled.styledTag

fun RBuilder.styledPath(
    id: String? = null,
    d: String,
    filter: String? = null,
    block: StyledDOMBuilder<PATH>.() -> Unit
) =
    styledTag(block) {
        PATH(attributesMapOf("id", id, "d", d, "filter", filter), it)
    }


fun RBuilder.path(id: String? = null, d: String, filter: String? = null) =
    tag({}) { PATH(attributesMapOf("id", id, "d", d, "filter", filter), it) }

class PATH(initialAttributes: Map<String, String>, override val consumer: TagConsumer<*>) :
    HTMLTag("path", consumer, initialAttributes, null, false, true)