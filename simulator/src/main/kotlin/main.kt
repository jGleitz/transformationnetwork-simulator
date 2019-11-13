package de.joshuagleitze.transformationnetwork.simulator

import Persons2GuestsTransformation
import de.joshuagleitze.transformationnetwork.changeablemodel.factory.model
import de.joshuagleitze.transformationnetwork.changeablemodel.planChanges
import de.joshuagleitze.transformationnetwork.models.guestlist.GuestlistMetamodel
import de.joshuagleitze.transformationnetwork.models.persons.Person
import de.joshuagleitze.transformationnetwork.models.persons.PersonsMetamodel
import de.joshuagleitze.transformationnetwork.simulator.components.model.at
import de.joshuagleitze.transformationnetwork.simulator.components.model.x
import de.joshuagleitze.transformationnetwork.simulator.components.simulator.TransformationSimulator
import de.joshuagleitze.transformationnetwork.simulator.data.scenario.SimulatorScenario
import de.joshuagleitze.transformationnetwork.simulator.styles.globalStyleSheet
import react.dom.render
import styled.StyledComponents
import styled.injectGlobal
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Date
import kotlin.js.Promise

private val scenarios = arrayOf(
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
            transformations = listOf(
                Persons2GuestsTransformation.create(nicePersons, guestlist),
                Persons2GuestsTransformation.create(family, guestlist)
            ),
            changes = planChanges(listOf(nicePersons, family, guestlist), {
                nicePersons += Person().apply {
                    firstName = "Martin"
                    lastName = "Mustermann"
                    birthDate = Date(1991, 4, 11)
                }
            })
        )
    }(),
    SimulatorScenario(
        "Single Model", listOf(PersonsMetamodel.model("Single") at (1 x 1)), listOf(), listOf()
    )
)

fun main() {
    window.onload = {
        val root = document.getElementById("root") ?: throw IllegalStateException()
        StyledComponents.injectGlobal(globalStyleSheet.toString())

        Promise.resolve(Unit).then {
            render(root) {
                TransformationSimulator(*scenarios)
            }
        }
    }
}