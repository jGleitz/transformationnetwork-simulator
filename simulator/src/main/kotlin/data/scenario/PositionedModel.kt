package de.joshuagleitze.transformationnetwork.simulator.data.scenario

import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecordingModel
import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.simulator.data.scenario.ModelPosition

interface PositionedModel : ChangeRecordingModel {
    val model: Model
    val position: ModelPosition

    override fun copy(): PositionedModel
}