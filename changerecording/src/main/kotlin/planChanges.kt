package de.joshuagleitze.transformationnetwork.changerecording

import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.ChangeSet
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.DefaultAdditiveChangeSet
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecording.ApplyMode.RECORD_ONLY


fun planChanges(
    models: List<ChangeRecordingModel> = emptyList(),
    objects: List<ChangeRecordingModelObject> = emptyList(),
    vararg changers: () -> Unit
): List<ChangeSet> {
    check(models.isNotEmpty()) { "no models passed!" }
    return changers.map { changer ->
        val resultSet = DefaultAdditiveChangeSet()
        var blockToUse = changer
        blockToUse = models.fold(blockToUse) { prev, mod ->
            { resultSet += mod.recordChanges(mode = RECORD_ONLY) { prev() } }
        }
        blockToUse = objects.fold(blockToUse) { prev, obj ->
            { resultSet += obj.recordChanges(mode = RECORD_ONLY) { prev() } }
        }
        blockToUse()
        resultSet
    }
}