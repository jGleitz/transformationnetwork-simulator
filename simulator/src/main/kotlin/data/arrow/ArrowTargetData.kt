package de.joshuagleitze.transformationnetwork.simulator.data.arrow

import de.joshuagleitze.transformationnetwork.simulator.util.geometry.Angle
import de.joshuagleitze.transformationnetwork.simulator.util.geometry.Angle.Companion.arctangent
import de.joshuagleitze.transformationnetwork.simulator.util.geometry.Coordinate

interface ArrowTargetData {
    val width: Double
    val height: Double
    val left: Double
    val right: Double
    val top: Double
    val bottom: Double
    val cornerRounding: CornerRoundings

    val center: Coordinate
    val topLeft: Coordinate
    val topRight: Coordinate
    val bottomLeft: Coordinate
    val bottomRight: Coordinate

    fun projectToBorder(angle: Angle, distance: Double = .0): Coordinate {
        val topRightAngle = arctangent(height / width)
        val bottomLeftAngle = topRightAngle - Angle.PI
        val bottomRightAngle = -topRightAngle
        val topLeftAngle = bottomRightAngle - Angle.PI
        val pointOnBorder = when (angle) {
            in topRightAngle..topLeftAngle -> Coordinate(
                center.x - (angle - Angle.PI / 2).tangent() * (height / 2),
                top
            )
            in topLeftAngle..bottomLeftAngle -> Coordinate(
                left,
                center.y + (angle - Angle.PI).tangent() * (width / 2)
            )
            in bottomLeftAngle..bottomRightAngle -> Coordinate(
                center.x + (angle + Angle.PI / 2).tangent() * (height / 2),
                bottom
            )
            else -> Coordinate(
                right,
                center.y - angle.tangent() * (width / 2)
            )
        }

        return if (distance != .0) {
            pointOnBorder + (pointOnBorder - center).normalize() * distance
        } else {
            pointOnBorder
        }
    }
}