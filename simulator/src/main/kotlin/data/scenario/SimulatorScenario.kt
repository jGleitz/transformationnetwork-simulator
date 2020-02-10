package de.joshuagleitze.transformationnetwork.simulator.data.scenario

import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.ChangeSet
import de.joshuagleitze.transformationnetwork.changerecording.ObservableModelTransformation
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation

class SimulatorScenario(
    val name: String,
    models: List<PositionedModel>,
    transformations: Set<ObservableModelTransformation>,
    val changes: List<ChangeSet>
) {
    private val originalModels = models
    private val originalTransformations = transformations
    private lateinit var currentNetwork: PositionedTransformationNetwork
    val network get() = currentNetwork

    init {
        for (transformation in transformations) {
            check(models.any { it.model == transformation.leftModel }) { "The left model of '$transformation' is not part of the models!" }
            check(models.any { it.model == transformation.rightModel }) { "The right model of '$transformation' is not part of the models!" }
        }
        reset()
    }

    fun reset() {
        val newModelMap = originalModels.associate { it.model to it.copy() }
        val currentTransformations = originalTransformations.mapTo(HashSet()) {
            it.type.create(
                leftModel = (newModelMap[it.leftModel] ?: error("cannot find new left model")).model,
                rightModel = (newModelMap[it.rightModel] ?: error("cannot find new right model")).model
            )
        }
        val currentModels = originalModels.map { newModelMap[it.model] ?: error("") }
        currentNetwork = PositionedTransformationNetwork(currentModels, currentTransformations)
    }
}