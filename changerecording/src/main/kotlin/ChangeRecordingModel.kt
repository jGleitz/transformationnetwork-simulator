package de.joshuagleitze.transformationnetwork.changerecording

import de.joshuagleitze.transformationnetwork.changemetamodel.ModelObjectChange
import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObjectIdentifier
import de.joshuagleitze.transformationnetwork.publishsubscribe.Observable

interface ChangeRecordingModel : Model, ChangeRecording {
    override val objects: Set<ChangeRecordingModelObject>
    override fun copy(): ChangeRecordingModel
    override val directChanges: Observable<ModelObjectChange>
    override fun getObject(identifier: ModelObjectIdentifier): ChangeRecordingModelObject?
}