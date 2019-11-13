package de.joshuagleitze.transformationnetwork.simulator.components.simulator

import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.simulator.components.model.ModelCanvas
import de.joshuagleitze.transformationnetwork.simulator.components.transformation.TransformationCanvas
import de.joshuagleitze.transformationnetwork.simulator.data.scenario.SimulatorScenario
import de.joshuagleitze.transformationnetwork.simulator.util.checkAvailable
import kotlinext.js.jsObject
import kotlinx.css.Display.grid
import kotlinx.css.GridRowStart
import kotlinx.css.GridTemplateRows
import kotlinx.css.Position
import kotlinx.css.display
import kotlinx.css.gridRowStart
import kotlinx.css.gridTemplateRows
import kotlinx.css.height
import kotlinx.css.pct
import kotlinx.css.position
import kotlinx.css.width
import kotlinx.css.zIndex
import react.RBuilder
import react.RComponent
import react.RProps
import react.RReadableRef
import react.RState
import react.createRef
import react.setState
import styled.StyleSheet
import styled.css
import styled.styledDiv

private object TransformationSimulatorStyles : StyleSheet("TransformationSimulator") {
    val simulator by css {
        display = grid
        gridTemplateRows = GridTemplateRows("[selector] auto [controls] auto [canvas] 1fr")
        height = 100.pct
        width = 100.pct
    }
    val scenarioSelect by css {
        gridRowStart = GridRowStart("selector")
        zIndex = 100
    }
    val scenarioControls by css {
        gridRowStart = GridRowStart("controls")
    }
    val canvasOuter by css {
        height = 100.pct
        width = 100.pct
        position = Position.relative
        gridRowStart = GridRowStart("canvas")
    }
}

private interface TransformationSimulatorState : RState {
    var modelCanvasRef: RReadableRef<ModelCanvas>
    var scenarioIndex: Int
}

private interface TransformationSimulatorProps : RProps {
    var scenarios: List<SimulatorScenario>
}

private class TransformationSimulator : RComponent<TransformationSimulatorProps, TransformationSimulatorState>() {
    init {
        state = jsObject {
            modelCanvasRef = createRef()
            scenarioIndex = 0
        }
    }

    override fun RBuilder.render() {
        val currentScenario = props.scenarios[state.scenarioIndex]

        styledDiv {
            css { +TransformationSimulatorStyles.simulator }

            styledDiv {
                css { +TransformationSimulatorStyles.scenarioSelect }

                ScenarioSelector(props.scenarios, state.scenarioIndex, this@TransformationSimulator::setScenarioIndex)
            }

            styledDiv {
                css { +TransformationSimulatorStyles.scenarioControls }
            }

            styledDiv {
                css { +TransformationSimulatorStyles.canvasOuter }

                ModelCanvas(models = currentScenario.models) {
                    ref = state.modelCanvasRef
                }
                TransformationCanvas(
                    modelArrowTargetProvider = this@TransformationSimulator::getArrowTargetFromModelCanvas,
                    transformations = currentScenario.transformations
                )
            }
        }
    }

    private fun getArrowTargetFromModelCanvas(model: Model) =
        checkAvailable(state.modelCanvasRef.current).getArrowTarget(model)

    private fun setScenarioIndex(index: Int) {
        if (index != state.scenarioIndex) setState { scenarioIndex = index }
    }
}


fun RBuilder.TransformationSimulator(vararg scenario: SimulatorScenario) =
    child(TransformationSimulator::class) {
        attrs.scenarios = scenario.toList()
    }