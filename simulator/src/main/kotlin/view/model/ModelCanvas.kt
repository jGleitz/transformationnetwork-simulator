package de.joshuagleitze.transformationnetwork.simulator.view.model

import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.simulator.ArrowTarget
import de.joshuagleitze.transformationnetwork.simulator.styles.Dimension.baseSpacing
import kotlinx.css.Align.center
import kotlinx.css.BoxSizing.borderBox
import kotlinx.css.ColumnGap
import kotlinx.css.Display.grid
import kotlinx.css.JustifyContent.spaceEvenly
import kotlinx.css.Position.absolute
import kotlinx.css.RowGap
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
import styled.StyleSheet
import styled.css
import styled.styledDiv

private object ModelCanvasStyles : StyleSheet("ModelCanvasStyles") {
    val modelCanvas by css {
        position = absolute
        top = 0.px
        left = 0.px
        padding(all = baseSpacing * 4)
        boxSizing = borderBox
        width = 100.pct
        height = 100.pct
        display = grid
        rowGap = RowGap((baseSpacing * 2).value)
        columnGap = ColumnGap((baseSpacing * 2).value)
        alignItems = center
        put("justify-items", "center")
        justifyContent = spaceEvenly
        zIndex = 10
    }
}

interface ModelCanvasProps : RProps {
    var models: List<Model>
}

class ModelCanvas : RComponent<ModelCanvasProps, RState>() {
    private val modelArrowTargets: MutableMap<Model, ArrowTarget> = HashMap()

    override fun RBuilder.render() {
        styledDiv {
            css { +ModelCanvasStyles.modelCanvas }

            props.models.forEach { model ->
                ModelView(model) {
                    ref { modelArrowTargets[model] = it as ArrowTarget }
                }
            }
        }
    }

    fun getArrowTarget(model: Model) =
        checkNotNull(modelArrowTargets[model]) { "'$model' is not rendered on this canvas!" }
}

fun RBuilder.ModelCanvas(models: List<Model>, handler: RHandler<ModelCanvasProps>) = child(ModelCanvas::class) {
    attrs.models = models
    handler()
}
