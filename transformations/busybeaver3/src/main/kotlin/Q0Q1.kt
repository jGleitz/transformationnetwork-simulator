package de.joshuagleitze.transformationnetwork.transformations.busybeaver3

import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecordingModel
import de.joshuagleitze.transformationnetwork.changerecording.ObservableModelTransformationType
import de.joshuagleitze.transformationnetwork.changerecording.createChecked
import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.models.turingmachine.TuringMachineMetamodel
import implies

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
        '0' ->
            q0.ensureCanMoveRight()
                .copy(
                    timestamp = q0.timestamp + 1,
                    bandPosition = q0.bandPosition + 1,
                    band = q0.band.set(q0.bandPosition, '1')
                )
                .runIf({ currentChar == '1' }) {
                    val endIndex = q0.band.findFirstRight('0', from = bandPosition) ?: (band.lastIndex + 1)
                    copy(
                        timestamp = timestamp + 1,
                        bandPosition = endIndex,
                        band = band.set(bandPosition until endIndex, '1')
                    )
                }
                .fixBandRange('0')
        else -> q1
    }

    companion object Type : ObservableModelTransformationType {
        override val leftMetamodel get() = TuringMachineMetamodel
        override val rightMetamodel get() = TuringMachineMetamodel

        override fun create(leftModel: Model, rightModel: Model) = createChecked(leftModel, rightModel, ::Q0Q1)
    }
}
