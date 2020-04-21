package de.joshuagleitze.transformationnetwork.changerecording

import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformationType

inline fun ModelTransformationType.createChecked(
    leftModel: Model,
    rightModel: Model,
    createChecked: (ChangeRecordingModel, ChangeRecordingModel) -> ObservableModelTransformation
): ObservableModelTransformation {
    check(leftModel.metamodel == leftMetamodel) { "model '$leftModel' is not an instance of the left metamodel '$leftMetamodel'!" }
    check(rightModel.metamodel == rightMetamodel) { "model '$rightModel' is not an instance of the right metamodel '$rightMetamodel'!" }
    check(leftModel is ChangeRecordingModel) { "model '$leftModel' is not a ${ChangeRecordingModel::class.simpleName}!" }
    check(rightModel is ChangeRecordingModel) { "model '$rightModel' is not a ${ChangeRecordingModel::class.simpleName}!" }
    return createChecked(leftModel, rightModel)
}
