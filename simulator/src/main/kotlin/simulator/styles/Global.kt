package de.joshuagleitze.transformationnetwork.simulator.styles

import kotlinx.css.CSSBuilder
import kotlinx.css.body
import kotlinx.css.fontFamily
import kotlinx.css.margin
import kotlinx.css.padding
import kotlinx.css.px

val globalStyleSheet = CSSBuilder().apply {
    body {
        margin(all = 0.px)
        padding(all = 0.px)
        fontFamily = listOf(
            "-apple-system",
            "BlinkMacSystemFont",
            "Segoe UI",
            "Roboto",
            "Oxygen-Sans",
            "Ubuntu",
            "Cantarell",
            "Helvetica Neue",
            "sans-serif"
        ).joinToString(",") { if (it.contains(' ')) "\"$it\"" else it }
    }
}