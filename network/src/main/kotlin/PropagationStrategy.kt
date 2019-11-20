package de.joshuagleitze.transformationnetwork.network

import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.ChangeSet

interface PropagationStrategy {
    fun preparePropagation(changeSet: ChangeSet, network: TransformationNetwork): Propagation
}