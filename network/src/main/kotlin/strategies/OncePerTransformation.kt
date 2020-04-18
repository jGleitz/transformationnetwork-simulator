package de.joshuagleitze.transformationnetwork.network.strategies

import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.ChangeSet
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.DefaultAdditiveChangeSet
import de.joshuagleitze.transformationnetwork.network.PropagationStrategy
import de.joshuagleitze.transformationnetwork.network.TransformationNetwork
import de.joshuagleitze.transformationnetwork.network.buildPropagation
import de.joshuagleitze.transformationnetwork.network.executeTransformation

class OncePerTransformation : PropagationStrategy {
    override fun preparePropagation(changeSet: ChangeSet, network: TransformationNetwork) = buildPropagation {
        val candidates = HashSet(network.transformations)
        val allChanges = DefaultAdditiveChangeSet(changeSet)
        val candidatesInOrder = generateSequence {
            candidates.find { allChanges.affect(it) }
        }
        for (candidate in candidatesInOrder) {
            yield() {
                allChanges += executeTransformation(candidate, allChanges)
            }
            candidates -= candidate
        }
    }
}
