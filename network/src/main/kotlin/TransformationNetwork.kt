package de.joshuagleitze.transformationnetwork.network

import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecordingModel
import de.joshuagleitze.transformationnetwork.changerecording.ObservableModelTransformation
import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.metametamodel.ModelIdentity
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation

interface TransformationNetwork {
    val models: List<ChangeRecordingModel>
    val transformations: Set<ObservableModelTransformation>

    fun transformationEdgesOf(model: Model): Set<ObservableModelTransformation> =
        transformations.filterTo(HashSet()) { it.leftModel == model || it.rightModel == model }

    fun getModel(modelIdentity: ModelIdentity): ChangeRecordingModel?
    operator fun get(modelIdentity: ModelIdentity) = getModel(modelIdentity)
}