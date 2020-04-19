package de.joshuagleitze.transformationnetwork.transformations.busybeaver3

import de.joshuagleitze.transformationnetwork.changerecording.BaseModelTransformation
import de.joshuagleitze.transformationnetwork.metametamodel.ofType
import de.joshuagleitze.transformationnetwork.models.turingmachine.TuringState

abstract class TuringStateTransformation : BaseModelTransformation() {
    private val writableLeftState get() = leftModel.objects.ofType(TuringState.Metaclass).first()
    private val notNullLeftState get() = writableLeftState.let(::getNotNull)
    private val writableRightState get() = rightModel.objects.ofType(TuringState.Metaclass).first()
    private val notNullRightState get() = writableRightState.let(::getNotNull)
    protected val states get() = notNullLeftState?.let { left -> notNullRightState?.let { right -> Pair(left, right) } }

    final override fun processChangesChecked(leftSide: TransformationSide, rightSide: TransformationSide) {
        if (!isConsistent()) {
            if (leftSide.modifications.targetting(TuringState.Metaclass).isNotEmpty()) {
                writableRightState.updateWith(::createNextRightState)
            }
            if (rightSide.modifications.targetting(TuringState.Metaclass).isNotEmpty()) {
                writableLeftState.updateWith(::createNextLeftState)
            }
        }
    }

    private inline fun TuringState.updateWith(newStateCreator: (NonNullTuringState, NonNullTuringState) -> NonNullTuringState) =
        states?.let { (left, right) ->
            this.setTo(newStateCreator(left, right))
        }

    protected open fun createNextLeftState(
        leftState: NonNullTuringState,
        rightState: NonNullTuringState
    ) = leftState

    protected open fun createNextRightState(
        leftState: NonNullTuringState,
        rightState: NonNullTuringState
    ) = rightState
}
