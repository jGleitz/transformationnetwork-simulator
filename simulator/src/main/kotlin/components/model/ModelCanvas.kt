package de.joshuagleitze.transformationnetwork.simulator.components.model

import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.simulator.components.arrow.ArrowTarget
import de.joshuagleitze.transformationnetwork.simulator.data.scenario.PositionedModel
import de.joshuagleitze.transformationnetwork.simulator.styles.Dimension.baseSpacing
import kotlinx.css.Align.center
import kotlinx.css.BoxSizing.borderBox
import kotlinx.css.Display.grid
import kotlinx.css.Gap
import kotlinx.css.JustifyContent.spaceEvenly
import kotlinx.css.Position.absolute
import kotlinx.css.alignItems
import kotlinx.css.boxSizing
import kotlinx.css.columnGap
import kotlinx.css.display
import kotlinx.css.height
import kotlinx.css.justifyContent
import kotlinx.css.left
import kotlinx.css.padding
import kotlinx.css.pct
import kotlinx.css.position
import kotlinx.css.px
import kotlinx.css.rowGap
import kotlinx.css.top
import kotlinx.css.width
import kotlinx.css.zIndex
import react.RBuilder
import react.RComponent
import react.RHandler
import react.RProps
import react.RState
import react.key
import styled.StyleSheet
import styled.css
import styled.styledDiv

private object ModelCanvasStyles : StyleSheet("ModelCanvas") {
    val modelCanvas by css {
        position = absolute
        top = 0.px
        left = 0.px
        padding(all = baseSpacing * 4)
        boxSizing = borderBox
        width = 100.pct
        height = 100.pct
        display = grid
        rowGap = Gap((baseSpacing * 2).value)
        columnGap = Gap((baseSpacing * 2).value)
        alignItems = center
        put("justify-items", "center")
        justifyContent = spaceEvenly
        zIndex = 10
    }
}

interface ModelCanvasProps : RProps {
    var models: List<PositionedModel>
}

class ModelCanvas : RComponent<ModelCanvasProps, RState>() {
    private val modelArrowTargets: MutableMap<Model, ArrowTarget> = HashMap()

    override fun RBuilder.render() {
        styledDiv {
            css { +ModelCanvasStyles.modelCanvas }

            props.models.forEach { model ->
                ModelView(model) {
                    attrs.key = model.hashCode().toString()
                    ref { modelView: Any? ->
                        when (modelView) {
                            null -> modelArrowTargets.remove(model.model)
                            is ArrowTarget -> modelArrowTargets[model.model] = modelView
                            else -> throw IllegalStateException("$modelView is not an ArrowTarget!")
                        }
                    }
                }
            }
        }
    }

    fun getArrowTarget(model: Model) =
        checkNotNull(modelArrowTargets[model]) { "'$model' is not rendered on this canvas!" }
}

fun RBuilder.ModelCanvas(models: List<PositionedModel>, handler: RHandler<ModelCanvasProps>) =
    child(ModelCanvas::class) {
        attrs.models = models
        handler()
    }
