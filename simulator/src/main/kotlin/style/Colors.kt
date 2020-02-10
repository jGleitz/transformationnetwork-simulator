package de.joshuagleitze.transformationnetwork.simulator.styles

import kotlinx.css.Color
import kotlinx.css.Color.Companion.black
import kotlinx.css.Color.Companion.white
import kotlinx.css.hsl
import kotlinx.css.rgba

object Colors {
    val border = black
    val background = white
    val alternativeBackground = rgba(0, 0, 0, .14)
    val lessImportant = Color("#666666")
    val transformation = Color("#888888")
    val controlBorder = hsl(0, 0, 80)
    val controlHoverBorder = hsl(0, 0, 70)
    val controlBackground = hsl(0, 0, 100)
    val added = Color("#C6FFB3")
    val updated = Color("#FFEBCC")
    val executed = Color("#6699FF")
    val highlighted = Color("#CADCFF")
}
