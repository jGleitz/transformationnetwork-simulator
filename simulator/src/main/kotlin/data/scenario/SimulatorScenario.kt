package de.joshuagleitze.transformationnetwork.simulator.data.scenario

import de.joshuagleitze.transformationnetwork.changemetamodel.ChangeSet
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation
import de.joshuagleitze.transformationnetwork.simulator.components.model.PositionedModel

data class SimulatorScenario(
    val name: String,
    val models: List<PositionedModel>,
    val transformations: List<ModelTransformation>,
    val changes: List<ChangeSet>
)