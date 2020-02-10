package de.joshuagleitze.transformationnetwork.metametamodel

data class DefaultModelIdentity(private val value: Int, override val metamodel: Metamodel) : ModelIdentity {
    override fun identifies(candidate: Model) = candidate.identity == this

    override val identifyingString: String get() = "$metamodel#$value"

    override fun toString() = identifyingString
}

private var identityCount = 0
fun newIdentity(metamodel: Metamodel): ModelIdentity = DefaultModelIdentity(identityCount++, metamodel)