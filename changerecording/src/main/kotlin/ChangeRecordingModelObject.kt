package de.joshuagleitze.transformationnetwork.changerecording

import de.joshuagleitze.transformationnetwork.changemetamodel.AttributeSetChange
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObject
import de.joshuagleitze.transformationnetwork.publishsubscribe.Observable

typealias AnyChangeRecordingModelObject = ChangeRecordingModelObject<*>

interface ChangeRecordingModelObject<O : ChangeRecordingModelObject<O>> : ModelObject<O>, ChangeRecording {
    override val directChanges: Observable<AttributeSetChange<O, *>>
}
