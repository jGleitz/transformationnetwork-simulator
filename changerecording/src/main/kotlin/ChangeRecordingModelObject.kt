package de.joshuagleitze.transformationnetwork.changerecording

import de.joshuagleitze.transformationnetwork.changemetamodel.AttributeSetChange
import de.joshuagleitze.transformationnetwork.changemetamodel.ModelChange
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObject
import de.joshuagleitze.transformationnetwork.publishsubscribe.Observable

interface ChangeRecordingModelObject : ModelObject, ChangeRecording {
    override fun copy(): ChangeRecordingModelObject
    override val directChanges: Observable<AttributeSetChange<*>>
}