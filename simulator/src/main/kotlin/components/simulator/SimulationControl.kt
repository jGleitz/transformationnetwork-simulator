package de.joshuagleitze.transformationnetwork.simulator.components.simulator

import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.ChangeSet
import de.joshuagleitze.transformationnetwork.network.Propagation
import de.joshuagleitze.transformationnetwork.network.PropagationStrategy
import de.joshuagleitze.transformationnetwork.network.TransformationNetwork
import kotlinext.js.jsObject
import kotlinx.css.Display.inlineBlock
import kotlinx.css.LinearDimension.Companion.auto
import kotlinx.css.LinearDimension.Companion.maxContent
import kotlinx.css.TextAlign.center
import kotlinx.css.display
import kotlinx.css.em
import kotlinx.css.margin
import kotlinx.css.textAlign
import kotlinx.css.width
import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.RComponent
import react.RHandler
import react.RProps
import react.RState
import react.dom.button
import react.setState
import styled.StyleSheet
import styled.css
import styled.styledDiv
import styled.styledSpan

private object SimulationControlStyles : StyleSheet("SimulationControl") {
    val container by css {
        width = maxContent
        margin(horizontal = auto)
    }
    val timeDisplay by css {
        display = inlineBlock
        width = 3.em
        textAlign = center
    }
}

interface SimulationControlProps : RProps {
    var strategy: PropagationStrategy
    var changes: List<ChangeSet>
    var network: TransformationNetwork
    var setTime: (Int) -> Unit
    var resetSimulation: () -> Unit
}

private interface SimulationControlState : RState {
    var currentPropagation: Propagation?
    var nextChangeIndex: Int
}

private class SimulationControl : RComponent<SimulationControlProps, SimulationControlState>() {
    init {
        state = jsObject {
            currentPropagation = null
            nextChangeIndex = 0
        }
    }

    override fun RBuilder.render() {
        time.Consumer { time ->
            styledDiv {
                css { +SimulationControlStyles.container }

                button {
                    attrs.onClickFunction = { props.resetSimulation() }
                    +"reset"
                }

                styledSpan {
                    css { +SimulationControlStyles.timeDisplay }
                    +time.toString()
                }

                button {
                    attrs.disabled =
                        state.nextChangeIndex >= props.changes.size && state.currentPropagation?.isFinished() != false
                    attrs.onClickFunction = { next(time) }
                    +"next"
                }
            }
        }
    }

    private fun next(time: Int) {
        val currentPropagation = state.currentPropagation
        val nextStateIndex = state.nextChangeIndex
        if (currentPropagation == null) {
            if (nextStateIndex < props.changes.size) {
                val changes = props.changes[nextStateIndex]
                changes.forEach { change ->
                    console.log("applying $change")
                    change.applyTo(
                        props.network[change.targetModel]
                            ?: error("Cannot find the correct model instance for '$change'")
                    )
                }
                setState {
                    this.currentPropagation = props.strategy.preparePropagation(changes, props.network)
                    this.nextChangeIndex = nextStateIndex + 1
                }
            }
        } else {
            currentPropagation.propagateNext()
            if (currentPropagation.isFinished()) setState { this.currentPropagation = null }
        }
        props.setTime(time + 1)
    }
}

fun RBuilder.SimulationControl(handler: RHandler<SimulationControlProps> = {}) = child(SimulationControl::class) {
    handler()
}