package de.joshuagleitze.transformationnetwork.simulator.data.arrow

import de.joshuagleitze.transformationnetwork.simulator.util.geometry.Angle
import de.joshuagleitze.transformationnetwork.simulator.util.geometry.Line

fun lineBetween(start: ArrowTargetData, end: ArrowTargetData, spacing: Double = .0): Line {
    val angle = Angle.between(start.center, end.center)
    val startCoordinate = start.projectToBorder(angle, spacing)
    val endCoordinate = end.projectToBorder(angle - Angle.PI, spacing)
    return Line(startCoordinate, endCoordinate)
}