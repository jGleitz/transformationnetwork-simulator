package de.joshuagleitze.transformationnetwork.simulator

import de.joshuagleitze.transformationnetwork.network.strategies.ConstantPerTransformation
import de.joshuagleitze.transformationnetwork.network.strategies.ProvenanceAndReaction
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
import kotlinx.browser.document
import kotlinx.browser.window
import react.createElement
import react.dom.render
import styled.createGlobalStyle

private val scenarios = listOf(
    PersonsAndGuests.create(),
    ObjectOriented.create(),
    BusyBeaver3.create(),
    Counting.create(),
    A2B.create(4),
    A2B.create(5)
)

private val strategies = listOf(
    UnboundedPropagation().describe(name = "Unbounded Propagation"),
    ProvenanceAndReaction().describe(name = "Provenance and Reaction"),
    ConstantPerTransformation(1).describe(name = "1x per Transformation"),
    ConstantPerTransformation(3).describe(name = "3x per Transformation"),
    ConstantPerTransformation(4).describe(name = "4x per Transformation")
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
