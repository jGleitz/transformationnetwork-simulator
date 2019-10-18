package de.joshuagleitze.transformationnetwork.simulator.view.model

import de.joshuagleitze.transformationnetwork.metamodelling.Model
import de.joshuagleitze.transformationnetwork.simulator.styles.Colors
import de.joshuagleitze.transformationnetwork.simulator.styles.Dimension.baseSpacing
import de.joshuagleitze.transformationnetwork.simulator.styles.FontSize
import de.joshuagleitze.transformationnetwork.simulator.styles.FontSize.large
import de.joshuagleitze.transformationnetwork.simulator.styles.FontSize.normal
import kotlinx.css.BorderStyle.solid
import kotlinx.css.Display.inlineBlock
import kotlinx.css.LinearDimension.Companion.fitContent
import kotlinx.css.TextAlign.center
import kotlinx.css.borderColor
import kotlinx.css.borderRadius
import kotlinx.css.borderStyle
import kotlinx.css.borderWidth
import kotlinx.css.color
import kotlinx.css.display
import kotlinx.css.fontSize
import kotlinx.css.margin
import kotlinx.css.marginTop
import kotlinx.css.padding
import kotlinx.css.pct
import kotlinx.css.px
import kotlinx.css.textAlign
import kotlinx.css.width
import react.RBuilder
import styled.StyleSheet
import styled.css
import styled.styledDiv
import styled.styledH3
import styled.styledSpan

@Suppress("RemoveRedundantQualifierName")
private object ModelStyles : StyleSheet("ModelStyles", isStatic = true) {
    val modelContainer by css {
        padding(vertical = baseSpacing * 2, horizontal = baseSpacing * 1.5)
        borderStyle = solid
        borderWidth = 1.px
        borderColor = Colors.borderColor
        borderRadius = FontSize.normal
        width = fitContent
    }
    val modelName by css {
        fontSize = large
        textAlign = center
        display = inlineBlock
        width = 100.pct
        margin(all = 0.px)
    }
    val metamodelName by css {
        fontSize = normal
        color = Colors.lessImportant
        textAlign = center
        display = inlineBlock
        width = 100.pct
    }
    val objectContainer by css {
        width = fitContent
        marginTop = baseSpacing
    }
}

fun RBuilder.ModelView(model: Model) {
    styledDiv {
        css { +ModelStyles.modelContainer }
        styledH3 {
            css { +ModelStyles.modelName }
            +model.name
        }
        styledSpan {
            css { +ModelStyles.metamodelName }
            +": "
            +model.metamodel.name
        }
        styledDiv {
            css { +ModelStyles.objectContainer }
            model.objects.forEach { modelObject ->
                ModelObjectView(modelObject)
            }
        }
    }
}
