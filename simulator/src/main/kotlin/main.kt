package de.joshuagleitze.transformationnetwork.simulator

import Persons2GuestsTransformation
import de.joshuagleitze.transformationnetwork.changerecording.factory.model
import de.joshuagleitze.transformationnetwork.changerecording.planChanges
import de.joshuagleitze.transformationnetwork.models.guestlist.GuestlistMetamodel
import de.joshuagleitze.transformationnetwork.models.persons.Person
import de.joshuagleitze.transformationnetwork.models.persons.PersonsMetamodel
import de.joshuagleitze.transformationnetwork.network.strategies.NaivePropagationStrategy
import de.joshuagleitze.transformationnetwork.simulator.components.simulator.TransformationSimulator
import de.joshuagleitze.transformationnetwork.simulator.data.scenario.SimulatorScenario
import de.joshuagleitze.transformationnetwork.simulator.data.scenario.at
import de.joshuagleitze.transformationnetwork.simulator.data.scenario.x
import de.joshuagleitze.transformationnetwork.simulator.styles.globalStyleSheet
import react.dom.render
import styled.StyledComponents
import styled.injectGlobal
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Date

private val scenarios by lazy {
    listOf(
        {
            val nicePersons = PersonsMetamodel.model("Nice Persons")
            val family = PersonsMetamodel.model("Family")
            val guestlist = GuestlistMetamodel.model("Guest List")
            SimulatorScenario(
                "Persons and Guests",
                models = listOf(
                    nicePersons at (1 x 1),
                    guestlist at (2 x 2),
                    family at (1 x 3)
                ),
                transformations = setOf(
                    Persons2GuestsTransformation.create(nicePersons, guestlist),
                    Persons2GuestsTransformation.create(family, guestlist)
                ),
                changes = planChanges(listOf(nicePersons, family, guestlist), emptyList(), {
                    nicePersons += Person().apply {
                        firstName = "Martin"
                        lastName = "Mustermann"
                        birthDate = Date(1991, 4, 11)
                    }
                })
            )
        }(),
        SimulatorScenario(
            "Single Model", listOf(PersonsMetamodel.model("Single") at (1 x 1)), setOf(), listOf()
        )
    )
}

private val strategies = listOf(NaivePropagationStrategy())

fun main() {
    window.onload = {
        val root = document.getElementById("root") ?: throw IllegalStateException("Cannot find the root!")
        StyledComponents.injectGlobal(globalStyleSheet.toString())

        render(root) {
            TransformationSimulator(scenarios, strategies)
        }
    }
}