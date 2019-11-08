package de.joshuagleitze.transformationnetwork.modeltransformation

import de.joshuagleitze.transformationnetwork.metametamodel.Metamodel
import de.joshuagleitze.transformationnetwork.metametamodel.Model

interface ModelTransformationType {
    val leftMetamodel: Metamodel
    val rightMetamodel: Metamodel

    fun create(leftModel: Model, rightModel: Model): ModelTransformation
}