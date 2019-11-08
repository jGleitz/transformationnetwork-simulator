package de.joshuagleitze.transformationnetwork.simulator.styles

import kotlinx.css.CSSBuilder
import kotlinx.css.body
import kotlinx.css.fontFamily
import kotlinx.css.height
import kotlinx.css.html
import kotlinx.css.margin
import kotlinx.css.padding
import kotlinx.css.pct
import kotlinx.css.px
import kotlinx.css.width

val globalStyleSheet = CSSBuilder().apply {
    html {
        height = 100.pct
        width = 100.pct
    }
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
        height = 100.pct
        width = 100.pct
    }
    "#root" {
        height = 100.pct
        width = 100.pct
    }
}