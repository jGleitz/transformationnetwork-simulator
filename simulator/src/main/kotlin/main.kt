package de.joshuagleitze.transformationnetwork

import examplemodels.persons.Person
import react.dom.div
import react.dom.render
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Date

fun main() {
    window.onload = {
        val root = document.getElementById("root") ?: throw IllegalStateException()
        val martin = Person().apply {
            firstName = "Martin"
            lastName = "Mustermann"
            birthDate = Date(1991, 4, 8)
        }
        render(root) {
            div { +"Hi, my name is ${martin.firstName} ${martin.lastName}, I was born on ${martin.birthDate?.toDateString()}." }
        }
    }
}