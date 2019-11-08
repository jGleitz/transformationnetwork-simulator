package de.joshuagleitze.transformationnetwork.simulator.svgdsl

import kotlinx.html.HTMLTag
import kotlinx.html.TagConsumer
import kotlinx.html.attributesMapOf
import react.RBuilder
import react.dom.tag

fun RBuilder.feComposite(`in`: String, in2: String, result: String, operator: String) =
    tag({}) { FECOMPOSITE(attributesMapOf("in", `in`, "in2", in2, "result", result, "operator", operator), it) }

class FECOMPOSITE(initialAttributes: Map<String, String>, override val consumer: TagConsumer<*>) :
    HTMLTag("feComposite", consumer, initialAttributes, null, true, true) {
    companion object {
        object Operators {
            const val xor = "xor"
        }
    }
}

