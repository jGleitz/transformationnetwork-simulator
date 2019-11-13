package de.joshuagleitze.transformationnetwork.simulator.components.transformation

import de.joshuagleitze.transformationnetwork.simulator.util.geometry.Coordinate

interface SvgCoordinateSystem {
    fun domCoordinateToSvg(domCoordinate: Coordinate): Coordinate
}