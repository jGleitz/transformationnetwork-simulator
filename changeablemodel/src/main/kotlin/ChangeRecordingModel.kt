package de.joshuagleitze.transformationnetwork.changeablemodel

import de.joshuagleitze.transformationnetwork.metametamodel.Model

interface ChangeRecordingModel : Model, ChangeRecording {
    override val objects: Set<ChangeRecordingModelObject>
}