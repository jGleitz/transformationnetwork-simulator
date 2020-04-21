package de.joshuagleitze.transformationnetwork.simulator

import de.joshuagleitze.transformationnetwork.network.strategies.OncePerTransformation
import de.joshuagleitze.transformationnetwork.network.strategies.StepByStep
import de.joshuagleitze.transformationnetwork.network.strategies.UnboundedPropagation
import de.joshuagleitze.transformationnetwork.simulator.components.simulator.TransformationSimulator
import de.joshuagleitze.transformationnetwork.simulator.data.strategy.describe
import de.joshuagleitze.transformationnetwork.simulator.scenarios.A2B
import de.joshuagleitze.transformationnetwork.simulator.scenarios.BusyBeaver3
import de.joshuagleitze.transformationnetwork.simulator.scenarios.Counting
import de.joshuagleitze.transformationnetwork.simulator.scenarios.ObjectOriented
import de.joshuagleitze.transformationnetwork.simulator.scenarios.PersonsAndGuests
import de.joshuagleitze.transformationnetwork.simulator.styles.globalStyleSheet
import kotlinext.js.invoke
import kotlinext.js.js
import react.createElement
import react.dom.render
import styled.createGlobalStyle
import kotlin.browser.document
import kotlin.browser.window

private val scenarios = listOf(
    PersonsAndGuests.create(),
    ObjectOriented.create(),
    BusyBeaver3.create(),
    Counting.create(),
    A2B.create(5)
)

private val strategies = listOf(
    UnboundedPropagation().describe(name = "Unbounded Propagation"),
    StepByStep().describe(name = "Step by Step"),
    OncePerTransformation().describe(name = "Once per Transformation")
)

fun main() {
    window.onload = {
        val root = document.getElementById("root") ?: error("Cannot find the root!")

        render(root) {
            child(createElement(createGlobalStyle(globalStyleSheet.toString()), js {}))
            TransformationSimulator(scenarios, strategies)
        }
    }
}
