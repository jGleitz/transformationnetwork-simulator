package de.joshuagleitze.transformationnetwork.simulator.data.arrow

import de.joshuagleitze.transformationnetwork.simulator.util.geometry.Angle
import de.joshuagleitze.transformationnetwork.simulator.util.geometry.Traverse

fun lineBetweenWithMid(start: ArrowTargetData, end: ArrowTargetData, spacing: Double = .0): Traverse {
    val angle = Angle.between(start.center, end.center)
    val startCoordinate = start.projectToBorder(angle, spacing)
    val endCoordinate = end.projectToBorder(angle - Angle.PI, spacing)
    val midCoordinate = (startCoordinate + endCoordinate) / 2
    return Traverse(startCoordinate, midCoordinate, endCoordinate)
}
