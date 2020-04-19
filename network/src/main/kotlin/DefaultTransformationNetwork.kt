package de.joshuagleitze.transformationnetwork.network

import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecordingModel
import de.joshuagleitze.transformationnetwork.changerecording.ObservableModelTransformation
import de.joshuagleitze.transformationnetwork.metametamodel.ModelIdentity
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation

class DefaultTransformationNetwork(
    override val models: List<ChangeRecordingModel>,
    override val transformations: Set<ObservableModelTransformation>
) : TransformationNetwork {
    init {
        check(models.containsAll(transformations.neighbouringModels)) {
            val badTransformations = transformations
                .filter { !models.containsAll(it.models) }
                .associateWith { it.models - models }
                .entries
                .joinToString(separator = "\n") { "  ${it.key}: ${it.value}" }
            "Cannot use these transformation in this network because not all of their models are part of it:\n$badTransformations"
        }
    }

    override fun getModel(modelIdentity: ModelIdentity): ChangeRecordingModel? =
        models.find { modelIdentity.identifies(it) }

    override fun getTransformationBetween(first: ModelIdentity, second: ModelIdentity) =
        transformations.filter { transformation ->
            transformation.models.count { first.identifies(it) || second.identifies(it) } == 2
        }

    override fun subnetworkInducedBy(transformations: Set<ObservableModelTransformation>): TransformationNetwork {
        check(this.transformations.containsAll(transformations)) {
            val badTransformations = transformations - this.transformations
            "Cannot build a subnetwork because these transformations are not part of this network: $badTransformations"
        }
        val neighbouringModels = transformations.neighbouringModels
        @Suppress("UNCHECKED_CAST")
        return DefaultTransformationNetwork(
            models.filter { neighbouringModels.contains(it) },
            transformations
        )
    }

    private val Set<ModelTransformation>.neighbouringModels get() = this.flatMap { it.models }.toSet()
}
