package de.joshuagleitze.transformationnetwork.changerecording

import de.joshuagleitze.transformationnetwork.metametamodel.Metamodel
import de.joshuagleitze.transformationnetwork.metametamodel.Model

abstract class BaseModelTransformationType(
    override val leftMetamodel: Metamodel,
    override val rightMetamodel: Metamodel
) : ObservableModelTransformationType {
    final override fun create(leftModel: Model, rightModel: Model): ObservableModelTransformation {
        check(leftModel.metamodel == leftMetamodel) { "model '$leftModel' is not an instance of the left metamodel '$leftMetamodel'!" }
        check(rightModel.metamodel == rightMetamodel) { "model '$rightModel' is not an instance of the right metamodel '$rightMetamodel'!" }
        check(leftModel is ChangeRecordingModel) {"model '$leftModel' is not a ${ChangeRecordingModel::class.simpleName}!"}
        check(rightModel is ChangeRecordingModel) {"model '$rightModel' is not a ${ChangeRecordingModel::class.simpleName}!"}
        return createChecked(leftModel, rightModel)
    }

    protected abstract fun createChecked(leftModel: ChangeRecordingModel, rightModel: ChangeRecordingModel): ObservableModelTransformation
}