package de.joshuagleitze.transformationnetwork.simulator.data.scenario

import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecordingModel

private data class DefaultPositionedModel(
    override val model: ChangeRecordingModel,
    override val position: ModelPosition
) : PositionedModel, ChangeRecordingModel by model {
    override fun copy(): PositionedModel = DefaultPositionedModel(model.copy(), position)
    override fun toString() = "$model@$position"
}

infix fun ChangeRecordingModel.at(position: ModelPosition): PositionedModel = DefaultPositionedModel(this, position)
