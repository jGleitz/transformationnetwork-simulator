package de.joshuagleitze.transformationnetwork.simulator.data.scenario

import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecordingModel
import de.joshuagleitze.transformationnetwork.simulator.util.geometry.Coordinate

interface PositionedModel : ChangeRecordingModel {
    val model: ChangeRecordingModel
    val position: Coordinate

    override fun copy(): PositionedModel
}
