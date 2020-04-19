package de.joshuagleitze.transformationnetwork.simulator.data.scenario

import de.joshuagleitze.transformationnetwork.changerecording.ObservableModelTransformation
import de.joshuagleitze.transformationnetwork.metametamodel.ModelIdentity
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation
import de.joshuagleitze.transformationnetwork.network.TransformationNetwork

data class PositionedTransformationNetwork(
    override val models: List<PositionedModel>,
    override val transformations: Set<ObservableModelTransformation>
) : TransformationNetwork {
    init {
        val underlyingModels = models.map { it.model }
        check(underlyingModels.containsAll(transformations.neighbouringModels)) {
            val badTransformations = transformations
                .filter { !underlyingModels.containsAll(it.models) }
                .associateWith { it.models - underlyingModels }
                .entries
                .joinToString(separator = "\n") { "  ${it.key}: ${it.value}" }
            "Cannot use these transformation in this network because not all of their models are part of it:\n$badTransformations"
        }
    }

    override fun getModel(modelIdentity: ModelIdentity): PositionedModel? =
        models.find { modelIdentity.identifies(it) }

    override fun getTransformationBetween(first: ModelIdentity, second: ModelIdentity) =
        transformations.filter { transformation ->
            transformation.models.count { first.identifies(it) || second.identifies(it) } == 2
        }

    override fun subnetworkInducedBy(transformations: Set<ModelTransformation>): TransformationNetwork {
        check(this.transformations.containsAll(transformations)) {
            val badTransformations = transformations - this.transformations
            "Cannot build a subnetwork because these transformations are not part of this network: $badTransformations"
        }
        val neighbouringModels = transformations.neighbouringModels
        @Suppress("UNCHECKED_CAST")
        return PositionedTransformationNetwork(
            models.filter { neighbouringModels.contains(it.model) },
            transformations as Set<ObservableModelTransformation>
        )
    }

    private val Set<ModelTransformation>.neighbouringModels get() = this.flatMap { it.models }.toSet()
}
