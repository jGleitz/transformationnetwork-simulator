package de.joshuagleitze.transformationnetwork.simulator.components.transformation

import de.joshuagleitze.transformationnetwork.simulator.util.geometry.Coordinate
import org.w3c.dom.svg.SVGElement

data class DefaultSvgCoordinateSystem(private val domPosition: Coordinate) :
    SvgCoordinateSystem {
    override fun domCoordinateToSvg(domCoordinate: Coordinate) = (domCoordinate - domPosition)

    companion object {
        fun ofSvgElement(svgElement: SVGElement) = svgElement.getBoundingClientRect().let { svgRect ->
            DefaultSvgCoordinateSystem(Coordinate(svgRect.left, svgRect.top))
        }
    }
}