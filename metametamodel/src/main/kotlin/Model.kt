package de.joshuagleitze.transformationnetwork.metametamodel

interface Model {
    val metamodel: Metamodel
    val name: String
    val objects: Set<ModelObject>
    val identity: ModelIdentity

    fun addObject(modelObject: ModelObject): Model = apply { plusAssign(modelObject) }
    fun removeObject(modelObject: ModelObject): Model = apply { minusAssign(modelObject) }
    operator fun plusAssign(modelObject: ModelObject)
    operator fun minusAssign(modelObject: ModelObject)
    fun containsObject(identifier: ModelObjectIdentifier): Boolean = getObject(identifier) != null
    fun getObject(identifier: ModelObjectIdentifier): ModelObject?
    fun requireObject(identifier: ModelObjectIdentifier): ModelObject =
        getObject(identifier) ?: error("cannot find the object identified by $identifier!")

    fun copy(): Model
}