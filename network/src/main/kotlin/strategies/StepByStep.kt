package de.joshuagleitze.transformationnetwork.network.strategies

import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.ChangeSet
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.DefaultAdditiveChangeSet
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation
import de.joshuagleitze.transformationnetwork.network.PropagationScope
import de.joshuagleitze.transformationnetwork.network.PropagationStrategy
import de.joshuagleitze.transformationnetwork.network.TransformationNetwork
import de.joshuagleitze.transformationnetwork.network.buildPropagation
import de.joshuagleitze.transformationnetwork.network.executeTransformation

class StepByStep : PropagationStrategy {
    override fun preparePropagation(changeSet: ChangeSet, network: TransformationNetwork) = buildPropagation {
        propagate(changeSet, network)
    }

    private suspend fun PropagationScope.propagate(changes: ChangeSet, network: TransformationNetwork) {
        val candidates = HashSet(network.transformations)
        val executed = HashSet<ModelTransformation>()
        val allChanges = DefaultAdditiveChangeSet(changes)
        val candidatesInOrder = generateSequence {
            candidates.find { allChanges.affect(it) }
        }
        for (candidate in candidatesInOrder) {
            lateinit var candidateChanges: ChangeSet
            yield() {
                candidateChanges = executeTransformation(candidate, allChanges)
            }
            propagate(candidateChanges, network.subnetworkInducedBy(executed))
            allChanges += candidateChanges
            candidates -= candidate
            executed += candidate
        }
    }
}
