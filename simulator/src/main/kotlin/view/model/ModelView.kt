package de.joshuagleitze.transformationnetwork.simulator.view.model

import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.simulator.ArrowTarget
import de.joshuagleitze.transformationnetwork.simulator.styles.Colors
import de.joshuagleitze.transformationnetwork.simulator.styles.Dimension.baseSpacing
import de.joshuagleitze.transformationnetwork.simulator.styles.FontSize
import de.joshuagleitze.transformationnetwork.simulator.styles.FontSize.large
import de.joshuagleitze.transformationnetwork.simulator.styles.FontSize.normal
import kotlinext.js.jsObject
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
import org.w3c.dom.HTMLElement
import react.RBuilder
import react.RHandler
import react.RProps
import react.createRef
import react.forwardRef
import styled.StyleSheet
import styled.css
import styled.styledDiv
import styled.styledH3
import styled.styledSpan

@Suppress("RemoveRedundantQualifierName")
private object ModelStyles : StyleSheet("ModelStyles") {
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

interface ModelViewProps : RProps {
    var model: Model
}

private val modelView = forwardRef<ModelViewProps> { rawProps, forwardRef ->
    val props = rawProps.unsafeCast<ModelViewProps>()
    val containerRef = createRef<HTMLElement>()

    ArrowTarget(containerRef) {
        ref = forwardRef

        styledDiv {
            ref = containerRef

            css { +ModelStyles.modelContainer }
            styledH3 {
                css { +ModelStyles.modelName }
                +props.model.name
            }
            styledSpan {
                css { +ModelStyles.metamodelName }
                +": "
                +props.model.metamodel.name
            }
            styledDiv {
                css { +ModelStyles.objectContainer }
                props.model.objects.forEach { modelObject ->
                    ModelObjectView(modelObject)
                }
            }
        }
    }
}
/*
val modelViewWithForwardRef = forwardRef<ModelViewProps> { rawProps, forwardRef ->
    val props = rawProps as ModelViewProps
    child(ModelView::class) {
        attrs.model = props.model
        attrs.forwardRef = forwardRef
    }
}

class SubscribableModelView : RComponent<ModelViewProps, RState>() {
    override fun RBuilder.render() {
        val containerRef = createRef<HTMLElement>()
        ArrowTarget(containerRef) {
            modelViewWithForwardRef {
                attrs.model = props.model
                ref = containerRef
            }
        }
    }
}
*/

fun RBuilder.ModelView(model: Model, handler: RHandler<ModelViewProps>) = child(modelView, jsObject<ModelViewProps> {
    this.model = model
}) {
    handler()
}