package de.joshuagleitze.transformationnetwork.simulator.simulator

import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation
import de.joshuagleitze.transformationnetwork.simulator.checkAvailable
import de.joshuagleitze.transformationnetwork.simulator.view.model.ModelCanvas
import de.joshuagleitze.transformationnetwork.simulator.view.transformation.TransformationCanvas
import kotlinext.js.jsObject
import kotlinx.css.Position
import kotlinx.css.height
import kotlinx.css.pct
import kotlinx.css.position
import kotlinx.css.width
import react.RBuilder
import react.RComponent
import react.RProps
import react.RReadableRef
import react.RState
import react.createRef
import styled.StyleSheet
import styled.css
import styled.styledDiv

private object TransformationSimulatorStyles : StyleSheet("TransformationSimulatorStyles") {
    val canvasOuter by css {
        height = 100.pct
        width = 100.pct
        position = Position.relative
    }
}

private interface TransformationSimulatorState : RState {
    var modelCanvasRef: RReadableRef<ModelCanvas>
}

private interface TransformationSimulatorProps : RProps {
    var models: List<Model>
    var transformations: List<ModelTransformation>
}

private class TransformationSimulator : RComponent<TransformationSimulatorProps, TransformationSimulatorState>() {
    init {
        state = jsObject {
            modelCanvasRef = createRef()
        }
    }

    override fun RBuilder.render() {
        styledDiv {
            css { +TransformationSimulatorStyles.canvasOuter }

            ModelCanvas(models = props.models) {
                ref = state.modelCanvasRef
            }
            TransformationCanvas(
                modelArrowTargetProvider = this@TransformationSimulator::getArrowTargetFromModelCanvas,
                transformations = props.transformations
            )
        }
    }

    private fun getArrowTargetFromModelCanvas(model: Model) =
        checkAvailable(state.modelCanvasRef.current).getArrowTarget(model)
}


fun RBuilder.TransformationSimulator(models: List<Model>, transformations: List<ModelTransformation>) =
    child(TransformationSimulator::class) {
        attrs.models = models
        attrs.transformations = transformations
    }