package de.joshuagleitze.transformationnetwork.metametamodel

data class DefaultModelObjectIdentity(
    private val value: Int,
    override val metaclass: Metaclass
) : ModelObjectIdentity {
    override fun identifies(candidate: ModelObject) = candidate.identity == this
    override val identifyingString get() = "${metaclass.name}#$value"

    override fun toString() = identifyingString
}

private var instanceCount = 0

fun newObjectIdentity(metaclass: Metaclass) = DefaultModelObjectIdentity(instanceCount++, metaclass)