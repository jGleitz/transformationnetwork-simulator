package de.joshuagleitze.transformationnetwork

import de.joshuagleitze.transformationnetwork.models.persons.NicePersons
import de.joshuagleitze.transformationnetwork.simulator.styles.globalStyleSheet
import de.joshuagleitze.transformationnetwork.simulator.view.model.ModelView
import react.dom.render
import styled.StyledComponents
import styled.injectGlobal
import kotlin.browser.document
import kotlin.browser.window

fun main() {
    window.onload = {
        val root = document.getElementById("root") ?: throw IllegalStateException()
        StyledComponents.injectGlobal(globalStyleSheet.toString())
        render(root) {
            ModelView(NicePersons)
        }
    }
}