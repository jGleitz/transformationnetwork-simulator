package de.joshuagleitze.transformationnetwork.simulator.util.svg.elementdsl

import de.joshuagleitze.transformationnetwork.simulator.util.geometry.Coordinate
import kotlinx.html.HTMLTag
import kotlinx.html.TagConsumer
import kotlinx.html.attributesMapOf
import react.RBuilder
import react.dom.tag
import styled.StyledDOMBuilder
import styled.styledTag

fun RBuilder.styledCircle(
    id: String? = null,
    c: Coordinate,
    r: Double,
    block: StyledDOMBuilder<CIRCLE>.() -> Unit
) = styledCircle(id, c.x.toString(), c.y.toString(), r.toString(), block)


fun RBuilder.styledCircle(
    id: String? = null,
    cx: String,
    cy: String,
    r: String,
    block: StyledDOMBuilder<CIRCLE>.() -> Unit
) =
    styledTag(block) { consumer ->
        buildCircle(id, cx, cy, r, consumer)
    }

private fun buildCircle(
    id: String? = null,
    cx: String,
    cy: String,
    r: String,
    consumer: TagConsumer<*>
) =
    CIRCLE(attributesMapOf("id", id, "cx", cx, "cy", cy, "r", r), consumer)


fun RBuilder.circle(
    id: String? = null,
    position: Coordinate,
    radius: Double
) = circle(id, position.x.toString(), position.y.toString(), radius.toString())

fun RBuilder.circle(
    id: String? = null,
    cx: String,
    cy: String,
    r: String
) =
    tag({}) { consumer -> buildCircle(id, cx, cy, r, consumer) }

class CIRCLE(initialAttributes: Map<String, String>, override val consumer: TagConsumer<*>) :
    HTMLTag("circle", consumer, initialAttributes, null, false, true)
