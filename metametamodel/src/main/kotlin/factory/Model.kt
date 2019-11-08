package de.joshuagleitze.transformationnetwork.metametamodel.factory

import de.joshuagleitze.transformationnetwork.metametamodel.Metamodel
import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObject

fun Metamodel.model(name: String, vararg objects: ModelObject): Model {
    val metamodel = this
    objects.forEach {
        check(metamodel.classes.contains(it.metaclass)) { "$it’s metaclass ${it.metaclass} does not belong to $metamodel!" }
    }
    return ModelImpl(metamodel, name, *objects)
}

private class ModelImpl(
    override val metamodel: Metamodel,
    override val name: String,
    vararg objects: ModelObject
) : Model {
    private val _objects: MutableSet<ModelObject> = objects.toMutableSet()

    override val objects: Set<ModelObject> get() = _objects
    override fun plusAssign(modelObject: ModelObject) {
        check(metamodel.classes.contains(modelObject.metaclass)) { "$modelObject’s metaclass '${modelObject.metaclass}' does not belong to this model’s metamodel '$metamodel'!" }
        _objects += modelObject
    }

    override fun minusAssign(modelObject: ModelObject) {
        check(objects.contains(modelObject)) { "this model does not contain the model object '$modelObject'!" }
        _objects -= modelObject
    }
}