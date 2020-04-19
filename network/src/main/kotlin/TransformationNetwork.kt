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
    fun getModel(model: Model): ChangeRecordingModel? = getModel(model.identity)
    fun getTransformationBetween(first: ModelIdentity, second: ModelIdentity): Collection<ObservableModelTransformation>
    fun getTransformationBetween(first: Model, second: Model) =
        getTransformationBetween(first.identity, second.identity)

    operator fun get(model: Model) =
        checkNotNull(getModel(model)) { "Cannot find the corresponding model for $model" }

    operator fun get(models: Pair<Model, Model>): ObservableModelTransformation {
        val transformations = getTransformationBetween(this[models.first], this[models.second])
        check(transformations.size >= 0) { "There is no transformation between ${models.first} and ${models.second}!" }
        check(transformations.size <= 1) { "There are more than one transformations between ${models.first} and ${models.second}!" }
        return transformations.first()
    }

    fun subnetworkInducedBy(transformations: Set<ModelTransformation>): TransformationNetwork
}
