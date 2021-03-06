package de.joshuagleitze.transformationnetwork.models.turingmachine

import de.joshuagleitze.transformationnetwork.metametamodel.AbstractMetamodel
import de.joshuagleitze.transformationnetwork.metametamodel.Model

object TuringMachineMetamodel : AbstractMetamodel() {
    override val name: String get() = ""

    override val classes get() = setOf(TuringState.Metaclass)
}

fun <T : Model> T.withInitialState(initialBand: String = "", initialBandPosition: Int = 0) = this.apply {
    this += TuringState().apply {
        timestamp = -2
        band = initialBand
        bandPosition = initialBandPosition
    }
}
