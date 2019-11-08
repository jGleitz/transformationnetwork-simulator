package de.joshuagleitze.transformationnetwork.simulator.svgdsl

import de.joshuagleitze.transformationnetwork.simulator.Coordinate
import de.joshuagleitze.transformationnetwork.simulator.Line
import kotlinx.html.HTMLTag
import kotlinx.html.TagConsumer
import kotlinx.html.attributesMapOf
import react.RBuilder
import react.dom.tag
import styled.StyledDOMBuilder
import styled.styledTag

fun RBuilder.styledLine(x1: String, y1: String, x2: String, y2: String, block: StyledDOMBuilder<LINE>.() -> Unit) =
    styledTag(block) {
        LINE(attributesMapOf("x1", x1, "y1", y1, "x2", x2, "y2", y2), it)
    }

fun RBuilder.styledLine(start: Coordinate, end: Coordinate, block: StyledDOMBuilder<LINE>.() -> Unit) =
    styledLine(start.x.toString(), start.y.toString(), end.x.toString(), end.y.toString(), block)

fun RBuilder.styledLine(line: Line, block: StyledDOMBuilder<LINE>.() -> Unit) =
    styledLine(line.start, line.end, block)


fun RBuilder.line(x1: String, y1: String, x2: String, y2: String) =
    tag({}) { LINE(attributesMapOf("x1", x1, "y1", y1, "x2", x2, "y2", y2), it) }

fun RBuilder.line(start: Coordinate, end: Coordinate) =
    line(start.x.toString(), start.y.toString(), end.x.toString(), end.y.toString())

fun RBuilder.line(line: Line) = line(line.start, line.end)

class LINE(initialAttributes: Map<String, String>, override val consumer: TagConsumer<*>) :
    HTMLTag("line", consumer, initialAttributes, null, false, true)