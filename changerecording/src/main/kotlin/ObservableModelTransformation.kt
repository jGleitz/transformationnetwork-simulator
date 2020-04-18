package de.joshuagleitze.transformationnetwork.changerecording

import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation
import de.joshuagleitze.transformationnetwork.publishsubscribe.Observable

interface ObservableModelTransformation : ModelTransformation {
    val execution: Observable<Unit>
    override val type: ObservableModelTransformationType
    override val leftModel: ChangeRecordingModel
    override val rightModel: ChangeRecordingModel
    override val models: TransformationModels

    interface TransformationModels : ModelTransformation.TransformationModels {
        override fun get(side: ModelTransformation.Side): ChangeRecordingModel

        override fun iterator(): Iterator<ChangeRecordingModel>
    }
}
