package de.joshuagleitze.transformationnetwork.simulator.styles

import de.joshuagleitze.transformationnetwork.simulator.style.Font
import de.joshuagleitze.transformationnetwork.simulator.styles.Colors.controlBackground
import de.joshuagleitze.transformationnetwork.simulator.styles.Dimension.controlCornerRounding
import de.joshuagleitze.transformationnetwork.simulator.styles.Dimension.horizontalControlPadding
import de.joshuagleitze.transformationnetwork.simulator.styles.Dimension.verticalControlPadding
import kotlinx.css.BorderStyle.solid
import kotlinx.css.CSSBuilder
import kotlinx.css.backgroundColor
import kotlinx.css.body
import kotlinx.css.borderColor
import kotlinx.css.borderRadius
import kotlinx.css.borderStyle
import kotlinx.css.borderWidth
import kotlinx.css.button
import kotlinx.css.fontFamily
import kotlinx.css.fontSize
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
        fontFamily = Font.defaultFamilies
        fontSize = FontSize.normal
        height = 100.pct
        width = 100.pct
    }
    "#root" {
        height = 100.pct
        width = 100.pct
    }
    button {
        borderColor = Colors.controlBorder
        borderRadius = controlCornerRounding
        borderStyle = solid
        borderWidth = 1.px
        backgroundColor = controlBackground
        fontFamily = Font.defaultFamilies
        padding(vertical = verticalControlPadding, horizontal = horizontalControlPadding)

        hover {
            borderColor = Colors.controlHoverBorder
        }

        disabled {
            hover {
                borderColor = Colors.controlBorder
            }
        }
    }
}