package de.joshuagleitze.transformationnetwork.changeablemodel.factory

import de.joshuagleitze.transformationnetwork.changeablemodel.ChangeRecordingModel
import de.joshuagleitze.transformationnetwork.changeablemodel.ChangeRecordingModelObject
import de.joshuagleitze.transformationnetwork.changemetamodel.AdditionChange
import de.joshuagleitze.transformationnetwork.changemetamodel.DefaultAdditiveChangeSet
import de.joshuagleitze.transformationnetwork.changemetamodel.DeletionChange
import de.joshuagleitze.transformationnetwork.metametamodel.Metamodel
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObject

fun Metamodel.model(name: String, vararg objects: ChangeRecordingModelObject): ChangeRecordingModel {
    val metamodel = this
    objects.forEach {
        check(metamodel.classes.contains(it.metaclass)) { "$it’s metaclass ${it.metaclass} does not belong to $metamodel!" }
    }
    return DefaultChangeRecordingModel(metamodel, name, *objects)
}

private class DefaultChangeRecordingModel(
    override val metamodel: Metamodel,
    override val name: String,
    vararg objects: ChangeRecordingModelObject
) : ChangeRecordingModel {
    private var changeSet = DefaultAdditiveChangeSet()
    private val _objects: MutableSet<ChangeRecordingModelObject> = HashSet()
    override val objects: Set<ChangeRecordingModelObject> get() = _objects

    init {
        objects.forEach { modelObject ->
            if (modelObject is DefaultModelObject) {
                modelObject.model = this
            }
            this += modelObject
        }
    }

    override fun plusAssign(modelObject: ModelObject) {
        check(metamodel.classes.contains(modelObject.metaclass)) { "$modelObject’s metaclass '${modelObject.metaclass}' does not belong to this model’s metamodel '$metamodel'!" }
        if (_objects.add(modelObject as ChangeRecordingModelObject)) {
            changeSet.add(AdditionChange(this, modelObject))
        }
    }

    override fun minusAssign(modelObject: ModelObject) {
        check(objects.contains(modelObject)) { "this model does not contain the model object '$modelObject'!" }
        if (_objects.remove(modelObject as ChangeRecordingModelObject)) {
            changeSet.add(DeletionChange(this, modelObject))
        }
    }

    override fun getLastChanges() = if (_objects.isEmpty()) changeSet.copy()
    else {
        val result = changeSet.copy()
        for (modelObject in _objects) {
            result.addAll(modelObject.getLastChanges())
        }
        result
    }

    override fun resetLastChanges() {
        changeSet = DefaultAdditiveChangeSet()
        _objects.forEach { it.resetLastChanges() }
    }

    override fun toString() = name
}