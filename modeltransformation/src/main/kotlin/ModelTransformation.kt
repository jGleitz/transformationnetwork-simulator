package de.joshuagleitze.transformationnetwork.modeltransformation

import de.joshuagleitze.transformationnetwork.changemetamodel.ChangeSet
import de.joshuagleitze.transformationnetwork.metametamodel.Model

interface ModelTransformation {
    val leftModel: Model
    val rightModel: Model
    val models: TransformationModels

    fun processChanges(leftChanges: ChangeSet, rightChanges: ChangeSet)

    enum class Side {
        LEFT, RIGHT;

        val opposite
            get() = when (this) {
                LEFT -> RIGHT
                RIGHT -> LEFT
            }

        override fun toString() = this.name.toLowerCase()
    }

    interface TransformationModels {
        operator fun get(side: Side): Model
    }
}