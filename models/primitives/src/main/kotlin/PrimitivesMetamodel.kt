package de.joshuagleitze.transformationnetwork.models.primitives

import de.joshuagleitze.transformationnetwork.metametamodel.AbstractMetamodel
import de.joshuagleitze.transformationnetwork.metametamodel.Model

object PrimitivesMetamodel : AbstractMetamodel() {
    override val name: String get() = ""

    override val classes get() = setOf(Number.Metaclass, Word.Metaclass)
}

fun <M : Model> M.withNumber(number: Int) = apply {
    this += Number().apply { value = number }
}

fun <M : Model> M.withWord(word: String) = apply {
    this += Word().apply { value = word }
}
