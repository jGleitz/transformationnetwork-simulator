package de.joshuagleitze.transformationnetwork.simulator.svgdsl

import kotlinx.html.HTMLTag
import kotlinx.html.TagConsumer
import kotlinx.html.attributesMapOf
import react.RBuilder
import react.dom.tag

fun RBuilder.feMorphology(`in`: String, result: String, operator: String, radius: String) =
    tag({}) { FEMORPHOLOGY(attributesMapOf("in", `in`, "result", result, "operator", operator, "radius", radius), it) }

class FEMORPHOLOGY(initialAttributes: Map<String, String>, override val consumer: TagConsumer<*>) :
    HTMLTag("feMorphology", consumer, initialAttributes, null, true, true) {
    companion object {
        object Operators {
            const val dilate = "dilate"
        }

        object Inputs {
            const val SourceGraphic = "SourceGraphic"
        }
    }
}

