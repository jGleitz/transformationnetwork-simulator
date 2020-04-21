package de.joshuagleitze.transformationnetwork.network.strategies

import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.ChangeSet
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.DefaultAdditiveChangeSet
import de.joshuagleitze.transformationnetwork.network.PropagationStrategy
import de.joshuagleitze.transformationnetwork.network.TransformationNetwork
import de.joshuagleitze.transformationnetwork.network.buildPropagation
import de.joshuagleitze.transformationnetwork.network.executeTransformation

class ConstantPerTransformation(val maxPerTransformation: Int) : PropagationStrategy {
    override fun preparePropagation(changeSet: ChangeSet, network: TransformationNetwork) = buildPropagation {
        val candidates = network.transformations.associateWithTo(HashMap()) { 0 }
        val allChanges = DefaultAdditiveChangeSet(changeSet)
        val candidatesInOrder = generateSequence {
            candidates
                .filter { (candidate, count) ->
                    allChanges.affect(candidate) && count < maxPerTransformation && !candidate.isConsistent()
                }.minBy { (_, count) ->
                    count
                }
        }
        for ((candidate, count) in candidatesInOrder) {
            yield() {
                allChanges += executeTransformation(candidate, allChanges)
            }
            candidates[candidate] = count + 1
        }
    }
}
