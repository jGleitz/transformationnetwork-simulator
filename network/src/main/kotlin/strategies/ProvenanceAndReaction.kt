package de.joshuagleitze.transformationnetwork.network.strategies

import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.ChangeSet
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.DefaultAdditiveChangeSet
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.plus
import de.joshuagleitze.transformationnetwork.changerecording.ObservableModelTransformation
import de.joshuagleitze.transformationnetwork.network.PropagationScope
import de.joshuagleitze.transformationnetwork.network.PropagationStrategy
import de.joshuagleitze.transformationnetwork.network.TransformationNetwork
import de.joshuagleitze.transformationnetwork.network.buildPropagation
import de.joshuagleitze.transformationnetwork.network.executeTransformation

class ProvenanceAndReaction : PropagationStrategy {
    override fun preparePropagation(changeSet: ChangeSet, network: TransformationNetwork) = buildPropagation {
        propagate(changeSet, network)
    }

    private suspend fun PropagationScope.propagate(changes: ChangeSet, network: TransformationNetwork): ChangeSet {
        val candidates = HashSet(network.transformations)
        val executed = HashSet<ObservableModelTransformation>()
        val accumulatedChanges = DefaultAdditiveChangeSet(changes)
        val candidatesInOrder = generateSequence {
            candidates.find { accumulatedChanges.affect(it) && !it.isConsistent() }
        }
        for (candidate in candidatesInOrder) {
            lateinit var candidateChanges: ChangeSet
            yield() {
                candidateChanges = executeTransformation(candidate, accumulatedChanges)
            }
            val propagationChanges = propagate(candidateChanges, network.subnetworkInducedBy(executed))
            if (!candidate.isConsistent()) yield() {
                candidateChanges = executeTransformation(candidate, propagationChanges)
            }
            accumulatedChanges += propagationChanges + candidateChanges
            candidates -= candidate
            executed += candidate
        }
        return accumulatedChanges
    }
}
