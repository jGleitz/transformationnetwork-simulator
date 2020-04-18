package de.joshuagleitze.transformationnetwork.simulator.util.svg.elementdsl

import de.joshuagleitze.transformationnetwork.simulator.util.geometry.Coordinate
import de.joshuagleitze.transformationnetwork.simulator.util.geometry.Traverse
import kotlinx.html.HTMLTag
import kotlinx.html.TagConsumer
import kotlinx.html.attributesMapOf
import react.RBuilder
import react.dom.tag
import styled.StyledDOMBuilder
import styled.styledTag

fun RBuilder.styledPolyLine(coordinates: List<Coordinate>, block: StyledDOMBuilder<POLYLINE>.() -> Unit) =
    styledTag(block) { consumer -> buildLine(coordinates, consumer) }

fun RBuilder.styledPolyLine(vararg points: Coordinate, block: StyledDOMBuilder<POLYLINE>.() -> Unit) =
    styledPolyLine(points.asList(), block)

fun RBuilder.styledPolyLine(traverse: Traverse, block: StyledDOMBuilder<POLYLINE>.() -> Unit) =
    styledPolyLine(traverse.coordinates, block)


fun RBuilder.polyline(coordinates: List<Coordinate>) =
    tag({}) { consumer -> buildLine(coordinates, consumer) }

fun RBuilder.polyline(vararg points: Coordinate) = polyline(points.asList())

fun RBuilder.polyline(traverse: Traverse) = polyline(traverse.coordinates)

class POLYLINE(initialAttributes: Map<String, String>, override val consumer: TagConsumer<*>) :
    HTMLTag("polyline", consumer, initialAttributes, null, false, true)

private fun buildLine(coordinates: List<Coordinate>, consumer: TagConsumer<*>): POLYLINE {
    val pointsString = coordinates.joinToString(separator = " ") { p -> "${p.x},${p.y}" }
    return POLYLINE(attributesMapOf("points", pointsString), consumer)
}
