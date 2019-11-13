package de.joshuagleitze.transformationnetwork.simulator.data.arrow

data class DefaultCornerRoundings(
    override val topLeft: Double,
    override val topRight: Double,
    override val bottomRight: Double,
    override val bottomLeft: Double
) : CornerRoundings {
    constructor(topLeft: Number, topRight: Number, bottomRight: Number, bottomLeft: Number)
            : this(topLeft.toDouble(), topRight.toDouble(), bottomRight.toDouble(), bottomLeft.toDouble())
}