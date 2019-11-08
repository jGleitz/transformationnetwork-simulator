package de.joshuagleitze.transformationnetwork.simulator.styles

import kotlinx.css.px
import kotlin.math.floor

object Dimension {
    val baseSpacingPx = floor(FontSize.normalPx * (2.0 / 3))
    val baseSpacing = baseSpacingPx.px
}