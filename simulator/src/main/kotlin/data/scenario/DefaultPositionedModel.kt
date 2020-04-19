package de.joshuagleitze.transformationnetwork.simulator.data.scenario

import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecordingModel
import de.joshuagleitze.transformationnetwork.simulator.util.geometry.Coordinate

private data class DefaultPositionedModel(
    override val model: ChangeRecordingModel,
    override val position: Coordinate
) : PositionedModel, ChangeRecordingModel by model {
    init {
        check(position.isInteger()) { "Models must be position at integer coordinates!" }
    }

    override fun copy(): PositionedModel = DefaultPositionedModel(model.copy(), position)
    override fun toString() = "$model@$position"
}

infix fun ChangeRecordingModel.at(position: Coordinate): PositionedModel = DefaultPositionedModel(this, position)
