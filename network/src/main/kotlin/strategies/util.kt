package de.joshuagleitze.transformationnetwork.network.strategies

import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.ChangeSet
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation

internal fun ChangeSet.affect(transformation: ModelTransformation) =
    this.affectedModels.containsEither(transformation.leftModel.identity, transformation.rightModel.identity)

private fun <T> Collection<T>.containsEither(a: T, b: T) = this.contains(a) || this.contains(b)
