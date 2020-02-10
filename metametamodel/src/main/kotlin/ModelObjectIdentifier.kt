package de.joshuagleitze.transformationnetwork.metametamodel

interface ModelObjectIdentifier {
    val metaclass: Metaclass
    fun matches(modelObject: ModelObject): Boolean
}

class SameValuesModelObjectIdentifier(override val metaclass: Metaclass, private val attributes: MetaAttributeMap) :
    ModelObjectIdentifier {
    override fun matches(modelObject: ModelObject) =
        modelObject.metaclass == metaclass && metaclass.attributes.all { attribute ->
            modelObject[attribute] == attributes[attribute]
        }

    override fun toString() = "$metaclass($attributes)"
}

class ModelObjectPredicate(
    override val metaclass: Metaclass,
    private val predicate: (ModelObject) -> Boolean
) : ModelObjectIdentifier {
    override fun matches(modelObject: ModelObject) = predicate(modelObject)

    override fun toString() = "$metaclass by predicate"
}

class ModelObjectIdentifierByIdentity(private val identity: ModelObjectIdentity) : ModelObjectIdentifier {
    override val metaclass: Metaclass get() = identity.metaclass

    override fun matches(modelObject: ModelObject) = identity.identifies(modelObject)

    override fun toString() = identity.toString()
}

object ExternalMetaclass : Metaclass {
    override val name: String get() = "<external>"
    override val attributes: Set<MetaAttribute<*>> get() = emptySet()
    override val ownAttributes: Set<MetaAttribute<*>> get() = emptySet()
    override val superClasses: Set<Metaclass> get() = emptySet()

    override fun createNew(identity: ModelObjectIdentity?): ModelObject {
        throw NotImplementedError("This metaclass is externally defined!")
    }
}

class ExternalModelObjectIdentifier(private val identifier: String) : ModelObjectIdentifier {
    override val metaclass get() = ExternalMetaclass

    override fun matches(modelObject: ModelObject) = false

    override fun toString() = identifier
}

fun sameValuesAs(modelObject: ModelObject) =
    SameValuesModelObjectIdentifier(modelObject.metaclass, modelObject.attributes)

fun search(metaclass: Metaclass, predicate: (ModelObject) -> Boolean) =
    ModelObjectPredicate(metaclass, predicate)

fun byIdentity(modelObjectIdentity: ModelObjectIdentity) = ModelObjectIdentifierByIdentity(modelObjectIdentity)
fun byIdentity(modelObject: ModelObject) = byIdentity(modelObject.identity)