package de.joshuagleitze.transformationnetwork.simulator.data.scenario

import de.joshuagleitze.transformationnetwork.changerecording.ObservableModelTransformation
import de.joshuagleitze.transformationnetwork.metametamodel.ModelIdentity
import de.joshuagleitze.transformationnetwork.network.TransformationNetwork

data class PositionedTransformationNetwork(
    override val models: List<PositionedModel>,
    override val transformations: Set<ObservableModelTransformation>
) : TransformationNetwork {
    override fun getModel(modelIdentity: ModelIdentity): PositionedModel? =
        models.find { modelIdentity.identifies(it) }

    override operator fun get(modelIdentity: ModelIdentity) = getModel(modelIdentity)
}