package de.joshuagleitze.transformationnetwork.simulator.components.model

import de.joshuagleitze.transformationnetwork.metametamodel.ModelObject
import de.joshuagleitze.transformationnetwork.simulator.styles.Colors
import de.joshuagleitze.transformationnetwork.simulator.styles.Colors.backgroundGray
import de.joshuagleitze.transformationnetwork.simulator.styles.Dimension.baseSpacing
import kotlinx.css.BorderCollapse.collapse
import kotlinx.css.BorderStyle.solid
import kotlinx.css.FontStyle.Companion.italic
import kotlinx.css.TextAlign.right
import kotlinx.css.backgroundColor
import kotlinx.css.borderCollapse
import kotlinx.css.borderTopColor
import kotlinx.css.borderTopStyle
import kotlinx.css.borderTopWidth
import kotlinx.css.fontStyle
import kotlinx.css.marginTop
import kotlinx.css.padding
import kotlinx.css.px
import kotlinx.css.textAlign
import react.RBuilder
import react.dom.span
import react.dom.tbody
import styled.StyleSheet
import styled.css
import styled.styledSpan
import styled.styledTable
import styled.styledTd
import styled.styledTr
import kotlin.js.Date

private object ModelObjectStyles : StyleSheet("ModelObject") {
    val attributesTable by css {
        borderCollapse = collapse
        borderTopWidth = 1.px
        borderTopStyle = solid
        borderTopColor = Colors.borderColor
        marginTop = baseSpacing
    }
    val attributeRow by css {
        nthChild("even") {
            backgroundColor = backgroundGray
        }
    }
    private val trTopBottomSpacing = baseSpacing * 0.5
    private val trInnerSpacing = baseSpacing * 0.5
    private val trOuterSpacing = baseSpacing * 1.5
    val attributeName by css {
        textAlign = right
        padding(
            top = trTopBottomSpacing,
            bottom = trTopBottomSpacing,
            left = trOuterSpacing,
            right = trInnerSpacing
        )
    }
    val attributeValue by css {
        padding(
            top = trTopBottomSpacing,
            bottom = trTopBottomSpacing,
            left = trInnerSpacing,
            right = trOuterSpacing
        )
    }
    val nullValue by css {
        fontStyle = italic
    }
}

private fun RBuilder.nullValue() {
    styledSpan {
        css { +ModelObjectStyles.nullValue }
    }
}

private fun RBuilder.styledValue(value: Any?) {
    when (value) {
        null -> nullValue()
        is Date -> span { +value.toLocaleDateString() }
        else -> span { +value.toString() }
    }
}

fun RBuilder.ModelObjectView(modelObject: ModelObject) {
    styledTable {
        css { +ModelObjectStyles.attributesTable }
        tbody {
            modelObject.metaclass.attributes.forEach { attribute ->
                styledTr {
                    css { +ModelObjectStyles.attributeRow }
                    styledTd {
                        css { +ModelObjectStyles.attributeName }
                        +attribute.name
                        +":"
                    }
                    styledTd {
                        css { +ModelObjectStyles.attributeValue }
                        styledValue(modelObject[attribute])
                    }
                }
            }
        }
    }
}