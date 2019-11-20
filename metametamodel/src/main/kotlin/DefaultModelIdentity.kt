package de.joshuagleitze.transformationnetwork.metametamodel

data class DefaultModelIdentity(private val value: Int, override val metamodel: Metamodel) : ModelIdentity {
    override fun identifies(model: Model) = model.identity == this

    override fun toString() = "$metamodel#$value"
}

private var identityCount = 0
fun newIdentity(metamodel: Metamodel): ModelIdentity = DefaultModelIdentity(identityCount++, metamodel)