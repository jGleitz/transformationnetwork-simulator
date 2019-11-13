package de.joshuagleitze.transformationnetwork.simulator.data.arrow

import de.joshuagleitze.transformationnetwork.simulator.util.geometry.Coordinate

class DefaultArrowTargetData(
    override val bottomLeft: Coordinate,
    override val topRight: Coordinate
) : ArrowTargetData {
    override val left: Double get() = bottomLeft.x
    override val right: Double get() = topRight.x
    override val top: Double get() = topRight.y
    override val bottom: Double get() = bottomLeft.y
    override val center get() = (bottomLeft + topRight) / 2
    override val width get() = right - left
    override val height get() = bottom - top
    override val topLeft: Coordinate
        get() = Coordinate(
            left,
            top
        )
    override val bottomRight: Coordinate
        get() = Coordinate(
            right,
            bottom
        )
    override val cornerRounding get() = EqualCornerRoundings.ZERO

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as DefaultArrowTargetData

        if (topLeft != other.topLeft) return false
        if (bottomRight != other.bottomRight) return false

        return true
    }

    override fun hashCode(): Int {
        var result = topLeft.hashCode()
        result = 31 * result + bottomRight.hashCode()
        return result
    }
}