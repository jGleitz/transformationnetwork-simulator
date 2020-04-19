package de.joshuagleitze.transformationnetwork.simulator.data.scenario

import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecordingModel
import de.joshuagleitze.transformationnetwork.changerecording.ObservableModelTransformation
import de.joshuagleitze.transformationnetwork.metametamodel.ModelIdentity
import de.joshuagleitze.transformationnetwork.network.DefaultTransformationNetwork
import de.joshuagleitze.transformationnetwork.network.TransformationNetwork

class PositionedTransformationNetwork private constructor(
    val modelMap: Map<ChangeRecordingModel, PositionedModel>,
    val backingNetwork: TransformationNetwork
) : TransformationNetwork by backingNetwork {
    constructor(models: List<PositionedModel>, transformations: Set<ObservableModelTransformation>) :
        this(models.associateBy { it.model }, DefaultTransformationNetwork(models.map { it.model }, transformations))

    override val models: List<PositionedModel>
        get() = modelMap.values.toList()

    override fun getModel(modelIdentity: ModelIdentity): PositionedModel? =
        backingNetwork.getModel(modelIdentity)?.let { modelMap[it] }

    override fun subnetworkInducedBy(transformations: Set<ObservableModelTransformation>): PositionedTransformationNetwork {
        val newBackingNetwork = backingNetwork.subnetworkInducedBy(transformations)
        return PositionedTransformationNetwork(modelMap, newBackingNetwork)
    }
}
