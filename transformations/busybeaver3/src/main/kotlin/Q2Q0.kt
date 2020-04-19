package de.joshuagleitze.transformationnetwork.transformations.busybeaver3

import de.joshuagleitze.transformationnetwork.changerecording.BaseModelTransformationType
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecordingModel
import de.joshuagleitze.transformationnetwork.models.turingmachine.TuringMachineMetamodel

class Q2Q0(val q2Model: ChangeRecordingModel, val q0Model: ChangeRecordingModel) :
    TuringStateTransformation() {
    override val leftModel: ChangeRecordingModel get() = q2Model
    override val rightModel: ChangeRecordingModel get() = q0Model
    override val type get() = Type

    override fun isConsistent() = states?.let { (q2, q0) ->
        (q2.timestamp > q0.timestamp) implies {
            q2.currentChar != '1'
        } && (q0.timestamp == q2.timestamp + 1) implies {
            q0.isLeftOf(q2, 1)
                && q0.band[q0.bandPosition + 1] == '1'
        }
    } ?: true

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun createNextRightState(q2: NonNullTuringState, q0: NonNullTuringState) = when (q2.currentChar) {
        '1' -> q2.copy(
            timestamp = q2.timestamp + 1,
            bandPosition = q2.bandPosition - 1
        ).fixBandRange('0')
        else -> q0
    }

    companion object Type : BaseModelTransformationType(TuringMachineMetamodel, TuringMachineMetamodel) {
        override fun createChecked(leftModel: ChangeRecordingModel, rightModel: ChangeRecordingModel) =
            Q2Q0(leftModel, rightModel)
    }
}
