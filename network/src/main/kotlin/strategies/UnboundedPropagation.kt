package de.joshuagleitze.transformationnetwork.network.strategies

import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.ChangeSet
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.DefaultAdditiveChangeSet
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation
import de.joshuagleitze.transformationnetwork.network.PropagationStrategy
import de.joshuagleitze.transformationnetwork.network.TransformationNetwork
import de.joshuagleitze.transformationnetwork.network.buildPropagation
import de.joshuagleitze.transformationnetwork.network.executeTransformation

class UnboundedPropagation : PropagationStrategy {
    override fun preparePropagation(changeSet: ChangeSet, network: TransformationNetwork) = buildPropagation {
        val changeHistory = mutableListOf(changeSet)
        val transformationKnowledge = HashMap<ModelTransformation, Int>().withDefault { -1 }
        var transformationsToUpdate = HashSet(network.transformations)
        while (transformationsToUpdate.isNotEmpty()) {
            val transformation = transformationsToUpdate.first()
            val leftModel = transformation.leftModel
            val rightModel = transformation.rightModel
            val unseenChanges = changeHistory.changesSince(transformationKnowledge.getValue(transformation))
            if (!unseenChanges.affectedModels.containsEither(leftModel.identity, rightModel.identity)) {
                transformationsToUpdate.remove(transformation)
            } else {
                yield() {
                    val newChanges = executeTransformation(
                        transformation,
                        leftChanges = unseenChanges.filterByModel(leftModel.identity),
                        rightChanges = unseenChanges.filterByModel(rightModel.identity)
                    )
                    changeHistory.add(newChanges)
                }
                transformationsToUpdate = HashSet(network.transformations)
            }
            transformationKnowledge[transformation] = changeHistory.size - 1
        }
    }

    private fun List<ChangeSet>.changesSince(timestamp: Int): ChangeSet {
        check(timestamp < this.size) { "timestamp $timestamp is too large, current maximum is ${this.size}" }
        val result =
            DefaultAdditiveChangeSet()
        this.subList(timestamp + 1, this.size).forEach { result += it }
        return result
    }

    private fun <T> Collection<T>.containsEither(vararg elements: T) = elements.any { this.contains(it) }
}
