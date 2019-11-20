package de.joshuagleitze.transformationnetwork.network

import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.metametamodel.ModelIdentity
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation

interface TransformationNetwork {
    val models: List<Model>
    val transformations: Set<ModelTransformation>

    fun transformationEdgesOf(model: Model): Set<ModelTransformation> =
        transformations.filterTo(HashSet()) { it.leftModel == model || it.rightModel == model }

    fun getModel(modelIdentity: ModelIdentity): Model?
    operator fun get(modelIdentity: ModelIdentity) = getModel(modelIdentity)
}