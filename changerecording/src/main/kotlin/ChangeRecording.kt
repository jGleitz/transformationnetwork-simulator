package de.joshuagleitze.transformationnetwork.changerecording

import de.joshuagleitze.transformationnetwork.changemetamodel.ModelChange
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.ChangeSet
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecording.ApplyMode.RECORD_AND_APPLY
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecording.RecordDepth.RECURSIVE
import de.joshuagleitze.transformationnetwork.publishsubscribe.Observable

interface ChangeRecording {
    fun recordChanges(
        depth: RecordDepth = RECURSIVE,
        mode: ApplyMode = RECORD_AND_APPLY,
        block: () -> Unit
    ): ChangeSet

    val directChanges: Observable<ModelChange>

    enum class ApplyMode {
        RECORD_AND_APPLY,
        RECORD_ONLY
    }

    enum class RecordDepth {
        RECURSIVE,
        DIRECT_ONLY
    }
}