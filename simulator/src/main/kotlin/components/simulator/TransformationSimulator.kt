package de.joshuagleitze.transformationnetwork.simulator.components.simulator

import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.simulator.components.model.ModelCanvas
import de.joshuagleitze.transformationnetwork.simulator.components.transformation.TransformationCanvas
import de.joshuagleitze.transformationnetwork.simulator.data.Described
import de.joshuagleitze.transformationnetwork.simulator.data.scenario.SimulatorScenario
import de.joshuagleitze.transformationnetwork.simulator.data.strategy.DescribedPropagationStrategy
import de.joshuagleitze.transformationnetwork.simulator.styles.Dimension.baseSpacing
import de.joshuagleitze.transformationnetwork.simulator.util.checkAvailable
import encodeURIComponent
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
import kotlinx.css.marginTop
import kotlinx.css.padding
import kotlinx.css.paddingRight
import kotlinx.css.pct
import kotlinx.css.position
import kotlinx.css.px
import kotlinx.css.width
import kotlinx.css.zIndex
import org.w3c.dom.History
import org.w3c.dom.Location
import org.w3c.dom.events.Event
import org.w3c.dom.url.URL
import org.w3c.dom.url.URLSearchParams
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
import kotlin.browser.window

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
        marginTop = baseSpacing
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
    var strategies: List<DescribedPropagationStrategy>
}

private var resetCounter = 0

private const val strategyQueryParameter = "strategy"
private const val scenarioQueryParameter = "scenario"

private class TransformationSimulator(props: TransformationSimulatorProps) :
    RComponent<TransformationSimulatorProps, TransformationSimulatorState>(props) {
    override fun TransformationSimulatorState.init(props: TransformationSimulatorProps) {
        modelCanvasRef = createRef()
        scenarioIndex = findScenarioIndexFromQuery(props)
        strategyIndex = findStrategyIndexFromQuery(props)
        time = 0
        resetCount = resetCounter++
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

                        ScenarioSelector(props.scenarios, state.scenarioIndex, ::setScenarioQuery)
                    }

                    styledDiv {
                        css { +TransformationSimulatorStyles.parameterLabel }
                        +"Strategy: "
                    }

                    styledDiv {
                        css { +TransformationSimulatorStyles.parameterSelector }

                        StrategySelector(props.strategies, state.strategyIndex, ::setStrategyQuery)
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
                            timeStep = { this@TransformationSimulator.setTime(state.time + 1) }
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

    private fun setScenarioQuery(index: Int) {
        window.history.replaceQuery {
            set(scenarioQueryParameter, props.scenarios[index].name.toUrlFormat())
        }
        updateActiveScenarioAndStrategy(null)
    }

    private fun setStrategyQuery(index: Int) {
        window.history.replaceQuery {
            set(strategyQueryParameter, props.strategies[index].name.toUrlFormat())
        }
        updateActiveScenarioAndStrategy(null)
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

    override fun componentDidMount() {
        setScenarioQuery(state.scenarioIndex)
        setStrategyQuery(state.strategyIndex)
        window.addEventListener("popstate", updateActiveScenarioAndStrategy)
    }

    override fun componentWillUnmount() {
        window.removeEventListener("popstate", updateActiveScenarioAndStrategy)
    }

    val updateActiveScenarioAndStrategy = { _: Event? ->
        val newScenarioIndex = findScenarioIndexFromQuery()
        val newStrategyIndex = findStrategyIndexFromQuery()
        if (newScenarioIndex != state.scenarioIndex || newStrategyIndex != state.strategyIndex) resetSimulation {
            scenarioIndex = newScenarioIndex
            strategyIndex = newStrategyIndex
        }
    }

    private fun findScenarioIndexFromQuery(props: TransformationSimulatorProps = this.props) =
        props.scenarios.findIndexByUrlName(window.location.searchParams[scenarioQueryParameter]) ?: 0

    private fun findStrategyIndexFromQuery(props: TransformationSimulatorProps = this.props) =
        props.strategies.findIndexByUrlName(window.location.searchParams[strategyQueryParameter]) ?: 0

    private fun String.toUrlFormat() = encodeURIComponent(replace(Regex("\\W+"), "-"))

    private fun History.replaceQuery(block: URLSearchParams.() -> Unit) {
        val url = URL(window.location.toString())
        url.searchParams.block()
        this.replaceState(null, "", url.toString())
    }

    private val Location.searchParams get() = URLSearchParams(this.search)

    private operator fun URLSearchParams.get(name: String) = this.get(name)

    private fun <T : Described> List<T>.findIndexByUrlName(searchedUrlName: String?) =
        if (searchedUrlName == null) null
        else this.indexOfFirst { it.name.toUrlFormat() == searchedUrlName }.takeIf { it != -1 }
}

fun RBuilder.TransformationSimulator(
    scenarios: List<SimulatorScenario>,
    strategies: List<DescribedPropagationStrategy>
) = child(TransformationSimulator::class) {
    attrs.scenarios = scenarios
    attrs.strategies = strategies
}
