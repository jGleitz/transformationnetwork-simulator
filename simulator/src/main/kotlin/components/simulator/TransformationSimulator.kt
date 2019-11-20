package de.joshuagleitze.transformationnetwork.simulator.components.simulator

import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.network.PropagationStrategy
import de.joshuagleitze.transformationnetwork.simulator.components.model.ModelCanvas
import de.joshuagleitze.transformationnetwork.simulator.components.transformation.TransformationCanvas
import de.joshuagleitze.transformationnetwork.simulator.data.scenario.SimulatorScenario
import de.joshuagleitze.transformationnetwork.simulator.styles.Dimension.baseSpacing
import de.joshuagleitze.transformationnetwork.simulator.util.checkAvailable
import kotlinext.js.jsObject
import kotlinx.css.Align
import kotlinx.css.BoxSizing
import kotlinx.css.Display.flex
import kotlinx.css.Display.grid
import kotlinx.css.GridRowStart
import kotlinx.css.GridTemplateRows
import kotlinx.css.Position
import kotlinx.css.alignSelf
import kotlinx.css.boxSizing
import kotlinx.css.display
import kotlinx.css.flexGrow
import kotlinx.css.flexShrink
import kotlinx.css.gridRowStart
import kotlinx.css.gridTemplateRows
import kotlinx.css.height
import kotlinx.css.marginRight
import kotlinx.css.padding
import kotlinx.css.paddingRight
import kotlinx.css.pct
import kotlinx.css.position
import kotlinx.css.px
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
    val parameterSelectionArea by css {
        gridRowStart = GridRowStart("selector")
        zIndex = 100
        display = flex
        width = 100.pct
        padding(vertical = baseSpacing, horizontal = baseSpacing * 2)
        boxSizing = BoxSizing.borderBox
    }
    val parameterLabel by css {
        alignSelf = Align.center
        paddingRight = baseSpacing
        flexGrow = .0
        flexShrink = .0
    }
    val parameterSelector by css {
        flexGrow = 1.0
        marginRight = baseSpacing * 3
        ":last-child" {
            marginRight = 0.px
        }
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
    var strategyIndex: Int
    var time: Int
    var resetCount: Int
}

private interface TransformationSimulatorProps : RProps {
    var scenarios: List<SimulatorScenario>
    var strategies: List<PropagationStrategy>
}

private var resetCounter = 0

private class TransformationSimulator : RComponent<TransformationSimulatorProps, TransformationSimulatorState>() {
    init {
        state = jsObject {
            modelCanvasRef = createRef()
            scenarioIndex = 0
            strategyIndex = 0
            time = 0
            resetCount = resetCounter++
        }
    }

    override fun RBuilder.render() {
        val currentScenario = props.scenarios[state.scenarioIndex]
        val currentStrategy = props.strategies[state.strategyIndex]

        time.Provider(state.time) {
            styledDiv {
                css { +TransformationSimulatorStyles.simulator }

                styledDiv {
                    css { +TransformationSimulatorStyles.parameterSelectionArea }

                    styledDiv {
                        css { +TransformationSimulatorStyles.parameterLabel }
                        +"Scenario: "
                    }

                    styledDiv {
                        css { +TransformationSimulatorStyles.parameterSelector }

                        ScenarioSelector(props.scenarios, state.scenarioIndex, ::setScenarioIndex)
                    }

                    styledDiv {
                        css { +TransformationSimulatorStyles.parameterLabel }
                        +"Strategy: "
                    }

                    styledDiv {
                        css { +TransformationSimulatorStyles.parameterSelector }

                        StrategySelector(props.strategies, state.strategyIndex, ::setStrategyIndex)
                    }
                }

                styledDiv {
                    css { +TransformationSimulatorStyles.scenarioControls }

                    SimulationControl {
                        key = state.resetCount.toString()
                        with(attrs) {
                            strategy = currentStrategy
                            changes = currentScenario.changes
                            network = currentScenario.network
                            setTime = this@TransformationSimulator::setTime
                            resetSimulation = { this@TransformationSimulator.resetSimulation() }
                        }
                    }
                }

                styledDiv {
                    css { +TransformationSimulatorStyles.canvasOuter }

                    ModelCanvas(models = currentScenario.network.models) {
                        ref = state.modelCanvasRef
                    }
                    TransformationCanvas(
                        modelArrowTargetProvider = ::getArrowTargetFromModelCanvas,
                        transformations = currentScenario.network.transformations
                    )
                }
            }
        }
    }

    private fun setTime(time: Int) {
        if (time != state.time) setState { this.time = time }
    }

    private fun getArrowTargetFromModelCanvas(model: Model) =
        checkAvailable(state.modelCanvasRef.current).getArrowTarget(model)

    private fun setScenarioIndex(index: Int) {
        if (index != state.scenarioIndex) resetSimulation {
            scenarioIndex = index
        }
    }

    private fun setStrategyIndex(index: Int) {
        if (index != state.strategyIndex) resetSimulation {
            strategyIndex = index
        }
    }

    private fun resetSimulation(stateModifier: TransformationSimulatorState.() -> Unit = {}) {
        if (state.time != 0) {
            props.scenarios[state.scenarioIndex].reset()
        }
        setState {
            time = 0
            resetCount = resetCounter++
            stateModifier()
        }
    }
}


fun RBuilder.TransformationSimulator(scenarios: List<SimulatorScenario>, strategies: List<PropagationStrategy>) =
    child(TransformationSimulator::class) {
        attrs.scenarios = scenarios
        attrs.strategies = strategies
    }