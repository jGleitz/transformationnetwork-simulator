package de.joshuagleitze.transformationnetwork.metametamodel

typealias AnyModelObjectIdentifier = ModelObjectIdentifier<*>

interface ModelObjectIdentifier<O : ModelObject<O>> {
    val metaclass: Metaclass<O>
    fun matches(modelObject: AnyModelObject): Boolean

}

class SameValuesModelObjectIdentifier<O : ModelObject<O>>(
    override val metaclass: Metaclass<O>,
    private val attributes: MetaAttributeMap
) : ModelObjectIdentifier<O> {
    override fun matches(modelObject: AnyModelObject) =
        modelObject.metaclass == metaclass && metaclass.attributes.all { attribute ->
            modelObject[attribute] == attributes[attribute]
        }

    override fun toString() = "$metaclass($attributes)"
}

class ModelObjectPredicate<O : ModelObject<O>>(
    override val metaclass: Metaclass<O>,
    private val predicate: (O) -> Boolean
) : ModelObjectIdentifier<O> {
    override fun matches(modelObject: AnyModelObject) = modelObject.optionallyAs(metaclass)?.let(predicate) ?: false

    override fun toString() = "$metaclass by predicate"
}

class ModelObjectIdentifierByIdentity<O : ModelObject<O>>(private val identity: ModelObjectIdentity<O>) :
    ModelObjectIdentifier<O> {
    override val metaclass: Metaclass<O> get() = identity.metaclass

    override fun matches(modelObject: AnyModelObject) =
        modelObject.optionallyAs(metaclass)?.let { identity.identifies(it) } ?: false

    override fun toString() = identity.toString()
}

object ExternalMetaclass : Metaclass<Nothing> {
    override val name: String get() = "<external>"
    override val attributes: Set<MetaAttribute<*>> get() = emptySet()
    override val ownAttributes: Set<MetaAttribute<*>> get() = emptySet()
    override val superClasses: Set<AnyMetaclass> get() = emptySet()

    override fun createNew(identity: AnyModelObjectIdentity?): Nothing {
        throw NotImplementedError("This metaclass is externally defined!")
    }
}

class ExternalModelObjectIdentifier(private val identifier: String) : ModelObjectIdentifier<Nothing> {
    override val metaclass get() = ExternalMetaclass

    override fun matches(modelObject: AnyModelObject) = false

    override fun toString() = identifier
}

fun <O : ModelObject<O>> sameValuesAs(modelObject: ModelObject<O>): ModelObjectIdentifier<O> =
    SameValuesModelObjectIdentifier(modelObject.metaclass, modelObject.attributes)

fun <O : ModelObject<O>> search(metaclass: Metaclass<O>, predicate: (O) -> Boolean): ModelObjectIdentifier<O> =
    ModelObjectPredicate(metaclass, predicate)

fun <O : ModelObject<O>> byIdentity(modelObjectIdentity: ModelObjectIdentity<O>): ModelObjectIdentifier<O> =
    ModelObjectIdentifierByIdentity(modelObjectIdentity)

fun <O : ModelObject<O>> byIdentity(modelObject: O) = byIdentity(modelObject.identity)
