package de.joshuagleitze.transformationnetwork

import react.dom.div
import react.dom.render
import kotlin.browser.document
import kotlin.browser.window

fun main() {
    window.onload = {
        val root = document.getElementById("root") ?: throw IllegalStateException()
        render(root) {
            div { +"Hello React + Kotlin!" }
        }
    }
}