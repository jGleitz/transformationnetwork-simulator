package de.joshuagleitze.transformationnetwork.metametamodel

interface Model {
    val metamodel: Metamodel
    val name: String
    val objects: Set<AnyModelObject>
    val identity: ModelIdentity

    fun addObject(modelObject: AnyModelObject): Model = apply { plusAssign(modelObject) }
    fun removeObject(modelObject: AnyModelObject): Model = apply { minusAssign(modelObject) }
    operator fun plusAssign(modelObject: AnyModelObject)
    operator fun minusAssign(modelObject: AnyModelObject) = minusAssign(modelObject.identity)
    operator fun minusAssign(modelObject: AnyModelObjectIdentity)
    fun containsObject(identifier: AnyModelObjectIdentifier): Boolean = getObject(identifier) != null
    fun <O : ModelObject<O>> getObject(identifier: ModelObjectIdentifier<O>): O?
    fun <O : ModelObject<O>> requireObject(identifier: ModelObjectIdentifier<O>): O =
        getObject(identifier) ?: error("cannot find the object identified by $identifier!")

    fun copy(): Model
}
