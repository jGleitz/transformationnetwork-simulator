package de.joshuagleitze.transformationnetwork.network

import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.ChangeSet
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.plus
import de.joshuagleitze.transformationnetwork.changerecording.ObservableModelTransformation

typealias PropagationScope = SequenceScope<() -> Unit>

fun buildPropagation(block: suspend PropagationScope.() -> Unit): Propagation =
    IteratorBasedPropagation(iterator { block() })

fun executeTransformation(
    transformation: ObservableModelTransformation,
    leftChanges: ChangeSet,
    rightChanges: ChangeSet
): ChangeSet {
    val leftModel = transformation.leftModel
    val rightModel = transformation.rightModel
    lateinit var rightResultChanges: ChangeSet
    val leftResultChanges = leftModel.recordChanges {
        rightResultChanges = rightModel.recordChanges {
            transformation.processChanges(leftChanges, rightChanges)
        }
    }
    return leftResultChanges + rightResultChanges
}

private class IteratorBasedPropagation(private val iterator: Iterator<() -> Unit>) : Propagation {
    override fun isFinished() = !iterator.hasNext()

    override fun propagateNext() {
        iterator.next()()
    }
}
