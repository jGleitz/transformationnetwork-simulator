package de.joshuagleitze.transformationnetwork.simulator

import Persons2GuestsTransformation
import de.joshuagleitze.transformationnetwork.metametamodel.factory.model
import de.joshuagleitze.transformationnetwork.models.guestlist.GuestlistMetamodel
import de.joshuagleitze.transformationnetwork.models.persons.NicePersons
import de.joshuagleitze.transformationnetwork.simulator.simulator.TransformationSimulator
import de.joshuagleitze.transformationnetwork.simulator.styles.globalStyleSheet
import react.dom.render
import styled.StyledComponents
import styled.injectGlobal
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Promise

fun main() {
    window.onload = {
        val root = document.getElementById("root") ?: throw IllegalStateException()
        StyledComponents.injectGlobal(globalStyleSheet.toString())

        val niceGuestsModel = GuestlistMetamodel.model("Nice Guests")

        Promise.resolve(Unit).then {
            render(root) {
                TransformationSimulator(
                    models = listOf(NicePersons, niceGuestsModel),
                    transformations = listOf(Persons2GuestsTransformation.create(NicePersons, niceGuestsModel))
                )
            }
        }
    }
}