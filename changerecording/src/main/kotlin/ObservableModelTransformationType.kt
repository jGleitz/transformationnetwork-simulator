package de.joshuagleitze.transformationnetwork.changerecording

import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformationType

interface ObservableModelTransformationType : ModelTransformationType {
    override fun create(leftModel: Model, rightModel: Model): ObservableModelTransformation
}