package de.joshuagleitze.transformationnetwork.transformations.busybeaver3

import de.joshuagleitze.transformationnetwork.changerecording.BaseModelTransformationType
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecordingModel
import de.joshuagleitze.transformationnetwork.models.turingmachine.TuringMachineMetamodel

class Q0Qf(val q0Model: ChangeRecordingModel, val qfModel: ChangeRecordingModel) :
    TuringStateTransformation() {
    override val leftModel: ChangeRecordingModel get() = q0Model
    override val rightModel: ChangeRecordingModel get() = qfModel
    override val type get() = Type

    override fun isConsistent() = states?.let { (q0, qf) ->
        (q0.timestamp > qf.timestamp) implies {
            q0.currentChar != '1'
        } && (qf.timestamp == q0.timestamp + 1) implies {
            qf.bandPosition == q0.bandPosition + 1
                && qf.band[qf.bandPosition - 1] == '1'
        }
    } ?: true

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun createNextRightState(q0: NonNullTuringState, qf: NonNullTuringState): NonNullTuringState =
        when (q0.currentChar) {
            '1' ->
                q0.copy(
                    timestamp = q0.timestamp + 1,
                    bandPosition = q0.bandPosition + 1
                )
            else -> qf
        }

    companion object Type : BaseModelTransformationType(TuringMachineMetamodel, TuringMachineMetamodel) {
        override fun createChecked(leftModel: ChangeRecordingModel, rightModel: ChangeRecordingModel) =
            Q0Qf(leftModel, rightModel)
    }
}
