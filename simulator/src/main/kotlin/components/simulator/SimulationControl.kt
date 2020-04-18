package de.joshuagleitze.transformationnetwork.simulator.components.simulator

import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.ChangeSet
import de.joshuagleitze.transformationnetwork.network.Propagation
import de.joshuagleitze.transformationnetwork.network.PropagationStrategy
import de.joshuagleitze.transformationnetwork.network.TransformationNetwork
import de.joshuagleitze.transformationnetwork.simulator.styles.Dimension.baseSpacing
import de.joshuagleitze.transformationnetwork.simulator.styles.Dimension.horizontalControlPadding
import de.joshuagleitze.transformationnetwork.simulator.styles.Dimension.verticalControlPadding
import kotlinext.js.jsObject
import kotlinx.css.Display.grid
import kotlinx.css.Display.inlineBlock
import kotlinx.css.GridColumnStart
import kotlinx.css.GridRowStart
import kotlinx.css.GridTemplateColumns
import kotlinx.css.LinearDimension.Companion.auto
import kotlinx.css.display
import kotlinx.css.fr
import kotlinx.css.gridColumnStart
import kotlinx.css.gridRowStart
import kotlinx.css.gridTemplateColumns
import kotlinx.css.margin
import kotlinx.css.padding
import kotlinx.css.pct
import kotlinx.css.width
import kotlinx.html.HTMLTag
import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.RComponent
import react.RHandler
import react.RProps
import react.RState
import react.dom.RDOMBuilder
import react.dom.button
import react.dom.jsStyle
import react.setState
import styled.StyleSheet
import styled.css
import styled.styledDiv
import styled.styledSpan

private object SimulationControlStyles : StyleSheet("SimulationControl") {
    val container by css {
        width = 100.pct
        display = grid
        gridTemplateColumns = GridTemplateColumns(1.fr, auto, 1.fr)
    }
    val leftControls by css {
        put("justify-self", "end")
    }
    val centerControls by css {
        put("justify-self", "center")
    }
    val rightControls by css {
        put("justify-self", "start")
    }
    val overlayButtonContainer by css {
        display = grid

        children {
            gridRowStart = GridRowStart("1")
            gridColumnStart = GridColumnStart("1")
        }
    }
    val timeDisplay by css {
        display = inlineBlock
        padding(vertical = verticalControlPadding, horizontal = horizontalControlPadding)
        margin(horizontal = baseSpacing)
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

                styledDiv {
                    css { +SimulationControlStyles.leftControls }

                    button {
                        attrs.onClickFunction = { props.resetSimulation() }
                        +"reset"
                    }
                }

                styledDiv {
                    css { +SimulationControlStyles.centerControls }

                    styledSpan {
                        css { +SimulationControlStyles.timeDisplay }

                        +time.toString()
                    }
                }

                styledDiv {
                    css { +SimulationControlStyles.rightControls }

                    styledDiv {
                        css { +SimulationControlStyles.overlayButtonContainer }

                        val hasPropagationStep = state.currentPropagation?.isFinished() == false
                        val hasChange = state.nextChangeIndex < props.changes.size

                        button {
                            visibleIf(hasPropagationStep)
                            attrs.onClickFunction = { applyNextPropagationStep(time) }
                            +"next propagation step"
                        }
                        button {
                            visibleIf(hasChange && !hasPropagationStep)
                            attrs.onClickFunction = { applyNextChange(time) }
                            +"apply next change"
                        }
                        button {
                            visibleIf(!hasPropagationStep && !hasChange)
                            attrs.disabled = true
                            +"no more changes"
                        }
                    }
                }
            }
        }
    }

    private fun RDOMBuilder<HTMLTag>.visibleIf(condition: Boolean) {
        attrs.jsStyle["visibility"] = if (condition) "visible" else "hidden"
    }

    private fun applyNextChange(time: Int) {
        val nextStateIndex = state.nextChangeIndex
        check(nextStateIndex < props.changes.size) { "No more changes!" }
        val changes = props.changes[nextStateIndex]
        changes.forEach { change ->
            change.applyTo(
                props.network[change.targetModel]
                    ?: error("Cannot find the correct model instance for '$change'")
            )
        }
        setState {
            this.currentPropagation = props.strategy.preparePropagation(changes, props.network)
            this.nextChangeIndex = nextStateIndex + 1
        }
        props.setTime(time + 1)
    }

    private fun applyNextPropagationStep(time: Int) {
        val currentPropagation = state.currentPropagation
        checkNotNull(currentPropagation) { "No current propagation!" }
        currentPropagation.propagateNext()
        if (currentPropagation.isFinished()) setState { this.currentPropagation = null }
        props.setTime(time + 1)
    }
}

fun RBuilder.SimulationControl(handler: RHandler<SimulationControlProps> = {}) = child(SimulationControl::class) {
    handler()
}
