package de.joshuagleitze.transformationnetwork.modeltransformation

import de.joshuagleitze.transformationnetwork.metametamodel.Metamodel
import de.joshuagleitze.transformationnetwork.metametamodel.Model

abstract class DefaultModelTransformationType(
    override val leftMetamodel: Metamodel,
    override val rightMetamodel: Metamodel
) : ModelTransformationType {
    final override fun create(leftModel: Model, rightModel: Model): ModelTransformation {
        check(leftModel.metamodel == leftMetamodel) { "model '$leftModel' is not an instance of the left metamodel '$leftMetamodel'!" }
        check(rightModel.metamodel == rightMetamodel) { "model '$rightModel' is not an instance of the right metamodel '$rightMetamodel'!" }
        return createChecked(leftModel, rightModel)
    }

    protected abstract fun createChecked(leftModel: Model, rightModel: Model): ModelTransformation
}