package de.joshuagleitze.transformationnetwork.models.number

import de.joshuagleitze.transformationnetwork.metametamodel.AbstractMetamodel
import de.joshuagleitze.transformationnetwork.metametamodel.Model

object NumberMetamodel : AbstractMetamodel() {
    override val name: String get() = "Number"

    override val classes get() = setOf(Number.Metaclass)
}

fun <M : Model> M.withNumber(number: Int) = apply {
    this += Number().apply { value = number }
}
