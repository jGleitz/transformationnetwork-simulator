package de.joshuagleitze.transformationnetwork.metametamodel

data class DefaultModelObjectIdentity<O : ModelObject<O>>(
    private val value: Int,
    override val metaclass: Metaclass<O>
) : ModelObjectIdentity<O> {
    override fun identifies(candidate: AnyModelObject) = candidate.identity == this
    override val identifyingString get() = "${metaclass.name}#$value"

    override fun toString() = identifyingString
}

private var instanceCount = 0

fun <O : ModelObject<O>> newObjectIdentity(metaclass: Metaclass<O>): ModelObjectIdentity<O> =
    DefaultModelObjectIdentity(instanceCount++, metaclass)
