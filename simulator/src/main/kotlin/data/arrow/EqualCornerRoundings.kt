package de.joshuagleitze.transformationnetwork.simulator.data.arrow

data class EqualCornerRoundings(val rounding: Double) :
    CornerRoundings {
    constructor(rounding: Number) : this(rounding.toDouble())

    override val topLeft get() = rounding
    override val topRight get() = rounding
    override val bottomRight get() = rounding
    override val bottomLeft get() = rounding

    companion object {
        val ZERO = EqualCornerRoundings(.0)
    }
}