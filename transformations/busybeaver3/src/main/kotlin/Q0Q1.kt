package de.joshuagleitze.transformationnetwork.transformations.busybeaver3

import de.joshuagleitze.transformationnetwork.changerecording.BaseModelTransformationType
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecordingModel
import de.joshuagleitze.transformationnetwork.models.turingmachine.TuringMachineMetamodel

class Q0Q1(val q0Model: ChangeRecordingModel, val q1Model: ChangeRecordingModel) : TuringStateTransformation() {
    override val leftModel: ChangeRecordingModel get() = q0Model
    override val rightModel: ChangeRecordingModel get() = q1Model
    override val type get() = Type

    override fun isConsistent() = states?.let { (q0, q1) ->
        (q0.timestamp > q1.timestamp) implies {
            q0.currentChar != '0'
        } && (q1.timestamp == q0.timestamp + 1) implies {
            q1.bandPosition == q0.band.findFirstRight('1', from = q0.bandPosition + 1) ?: q0.band.length
                && q1.band.subSequence(q0.bandPosition + 1, q1.bandPosition).all { it == '1' }
        }
    } ?: true

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun createNextRightState(q0: NonNullTuringState, q1: NonNullTuringState) = when (q0.currentChar) {
        '0' -> {
            val newQ1 = q0.copy(
                timestamp = q0.timestamp + 1,
                bandPosition = q0.band.findFirstRight('0', from = q0.bandPosition + 1) ?: q0.band.lastIndex + 1
            ).fixBandRange('0')
            val bandReplaceRange = q0.bandPosition until newQ1.bandPosition
            newQ1.copy(
                band = newQ1.band.replaceRange(bandReplaceRange, "1".repeat(bandReplaceRange.count()))
            )
        }
        else -> q1
    }

    companion object Type : BaseModelTransformationType(TuringMachineMetamodel, TuringMachineMetamodel) {
        override fun createChecked(leftModel: ChangeRecordingModel, rightModel: ChangeRecordingModel) =
            Q0Q1(leftModel, rightModel)
    }
}
