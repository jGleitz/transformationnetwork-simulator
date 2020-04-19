package de.joshuagleitze.transformationnetwork.transformations.busybeaver3

import de.joshuagleitze.transformationnetwork.changerecording.BaseModelTransformationType
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecordingModel
import de.joshuagleitze.transformationnetwork.models.turingmachine.TuringMachineMetamodel

class Q1Q2(val q1Model: ChangeRecordingModel, val q2Model: ChangeRecordingModel) :
    TuringStateTransformation() {
    override val leftModel: ChangeRecordingModel get() = q1Model
    override val rightModel: ChangeRecordingModel get() = q2Model
    override val type get() = Type

    override fun isConsistent(): Boolean = states?.let { (q1, q2) ->
        (q1.timestamp > q2.timestamp) implies {
            q1.currentChar != '0'
        } && (q2.timestamp == q1.timestamp + 1) implies {
            q2.bandPosition == q1.band.findFirstLeft('1', from = q1.bandPosition + 1) ?: 0
                && (q2.bandPosition > q1.bandPosition) implies { q2.band[q1.bandPosition] == '0' }
                && (q2.bandPosition <= q1.bandPosition) implies {
                q2.band.subSequence(q2.bandPosition, q1.bandPosition).all { it == '1' }
            }
        }
    } ?: true

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun createNextRightState(q1: NonNullTuringState, q2: NonNullTuringState) = when (q1.currentChar) {
        '0' -> {
            var newQ2 = q1.copy(timestamp = q1.timestamp + 1)
                .ensureCanMoveRight('0')
            newQ2 = newQ2.copy(bandPosition = newQ2.band.findFirstLeft('1', from = q1.bandPosition + 1) ?: -1)
                .fixBandRange('0')
            val bandReplaceRange =
                if (newQ2.bandPosition < q1.bandPosition) (newQ2.bandPosition + 1)..(q1.bandPosition + 1)
                else IntRange.EMPTY
            newQ2.copy(band = newQ2.band.replaceRange(bandReplaceRange, "1".repeat(bandReplaceRange.count())))
        }
        else -> q2
    }

    companion object Type : BaseModelTransformationType(TuringMachineMetamodel, TuringMachineMetamodel) {
        override fun createChecked(leftModel: ChangeRecordingModel, rightModel: ChangeRecordingModel) =
            Q1Q2(leftModel, rightModel)
    }
}
