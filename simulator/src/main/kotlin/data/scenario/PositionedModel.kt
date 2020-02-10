package de.joshuagleitze.transformationnetwork.simulator.data.scenario

import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecordingModel

interface PositionedModel : ChangeRecordingModel {
    val model: ChangeRecordingModel
    val position: ModelPosition

    override fun copy(): PositionedModel
}