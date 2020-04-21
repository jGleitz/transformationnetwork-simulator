package de.joshuagleitze.transformationnetwork.transformations.busybeaver3

import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecordingModel
import de.joshuagleitze.transformationnetwork.changerecording.ObservableModelTransformationType
import de.joshuagleitze.transformationnetwork.changerecording.createChecked
import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.models.turingmachine.TuringMachineMetamodel
import implies

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
        '0' ->
            q1.ensureCanMoveRight('0')
                .copy(
                    timestamp = q1.timestamp + 1,
                    bandPosition = q1.bandPosition + 1
                )
                .runIf({ currentChar == '0' }) {
                    val leftIndex = band.findFirstLeft('1', from = bandPosition) ?: -1
                    copy(
                        timestamp = timestamp + 1,
                        bandPosition = band.findFirstLeft('1', from = bandPosition) ?: -1,
                        band = band.set((leftIndex + 1)..bandPosition, '1')
                    )
                }
                .fixBandRange('0')
        else -> q2
    }

    companion object Type : ObservableModelTransformationType {
        override val leftMetamodel get() = TuringMachineMetamodel
        override val rightMetamodel get() = TuringMachineMetamodel

        override fun create(leftModel: Model, rightModel: Model) = createChecked(leftModel, rightModel, ::Q1Q2)
    }
}
