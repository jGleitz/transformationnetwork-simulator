package de.joshuagleitze.transformationnetwork.simulator.data.strategy

import de.joshuagleitze.transformationnetwork.network.PropagationStrategy

internal class DefaultDescribedPropagationStrategy(strategy: PropagationStrategy, override val name: String) :
    DescribedPropagationStrategy, PropagationStrategy by strategy

fun PropagationStrategy.describe(name: String): DescribedPropagationStrategy =
    DefaultDescribedPropagationStrategy(this, name)
