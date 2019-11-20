package de.joshuagleitze.transformationnetwork.changerecording.factory

import de.joshuagleitze.transformationnetwork.changemetamodel.AdditionChange
import de.joshuagleitze.transformationnetwork.changemetamodel.DeletionChange
import de.joshuagleitze.transformationnetwork.changemetamodel.ModelObjectChange
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.AdditiveChangeSet
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.ChangeSet
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.DefaultAdditiveChangeSet
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.ModelSpecificAdditiveChangeSet
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecording
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecording.ApplyMode.RECORD_AND_APPLY
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecording.ApplyMode.RECORD_ONLY
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecording.RecordDepth.DIRECT_ONLY
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecording.RecordDepth.RECURSIVE
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecordingModel
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecordingModelObject
import de.joshuagleitze.transformationnetwork.metametamodel.Metamodel
import de.joshuagleitze.transformationnetwork.metametamodel.ModelIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObject
import de.joshuagleitze.transformationnetwork.metametamodel.newIdentity
import de.joshuagleitze.transformationnetwork.publishsubscribe.Observable
import de.joshuagleitze.transformationnetwork.publishsubscribe.PublishingObservable

fun Metamodel.model(name: String, vararg objects: ChangeRecordingModelObject): ChangeRecordingModel {
    val metamodel = this
    objects.forEach {
        check(metamodel.classes.contains(it.metaclass)) { "$it’s metaclass ${it.metaclass} does not belong to $metamodel!" }
    }
    return DefaultModel(name, metamodel, objects.asIterable())
}

private class DefaultModel private constructor(
    override val name: String,
    objects: Iterable<ChangeRecordingModelObject>,
    override val identity: ModelIdentity
) : ChangeRecordingModel {
    override val metamodel: Metamodel get() = identity.metamodel
    private var changeSet: AdditiveChangeSet? = null
    private val _objects: MutableSet<ChangeRecordingModelObject> = HashSet()
    override val objects: Set<ChangeRecordingModelObject> get() = _objects
    private val _changes = PublishingObservable<ModelObjectChange>()
    override val directChanges: Observable<ModelObjectChange> get() = _changes
    private var recordOnly = false

    constructor(name: String, metamodel: Metamodel, objects: Iterable<ChangeRecordingModelObject>)
            : this(name, objects, newIdentity(metamodel))

    init {
        objects.forEach { modelObject ->
            this += modelObject
        }
    }

    override fun plusAssign(modelObject: ModelObject) {
        check(metamodel.classes.contains(modelObject.metaclass)) { "$modelObject’s metaclass '${modelObject.metaclass}' does not belong to this model’s metamodel '$metamodel'!" }
        if (modelObject is DefaultModelObject && !recordOnly) {
            modelObject.model = this
        }
        modelObject as ChangeRecordingModelObject
        val isChange = if (!recordOnly) _objects.add(modelObject) else !_objects.contains(modelObject)
        if (isChange) addAndPublish(AdditionChange(this.identity, modelObject))
    }

    override fun minusAssign(modelObject: ModelObject) {
        check(objects.contains(modelObject)) { "this model does not contain the model object '$modelObject'!" }
        if (modelObject is DefaultModelObject && !recordOnly) {
            modelObject.model = null
        }
        modelObject as ChangeRecordingModelObject
        val isChange = if (!recordOnly) _objects.remove(modelObject) else _objects.contains(modelObject)
        if (isChange) addAndPublish(DeletionChange(this.identity, modelObject))
    }

    private fun addAndPublish(change: ModelObjectChange) {
        changeSet?.add(change)
        if (!recordOnly) {
            _changes.publishIfChanged(change)
        }
    }

    override fun getSameValuedObject(modelObject: ModelObject) =
        _objects.find {
            it.metaclass == modelObject.metaclass && it.metaclass.attributes.all { metaAttribute ->
                it[metaAttribute] == modelObject[metaAttribute]
            }
        }

    override fun copy() =
        DefaultModel(
            this.name,
            this._objects.map { it.copy() },
            this.identity
        )

    override fun toString() = name

    override fun recordChanges(
        depth: ChangeRecording.RecordDepth,
        mode: ChangeRecording.ApplyMode,
        block: () -> Unit
    ): ChangeSet {
        val thisChangeSet = ModelSpecificAdditiveChangeSet(this.identity)
        synchronized(this) {
            check(this.changeSet == null) { "There is already another change recording in progress for $this!" }
            this.changeSet = thisChangeSet
        }
        var blockToUse = block
        when (mode) {
            RECORD_AND_APPLY -> Unit
            RECORD_ONLY -> this.recordOnly = true
        }
        val resultChangeSet: AdditiveChangeSet
        when (depth) {
            DIRECT_ONLY -> resultChangeSet = thisChangeSet
            RECURSIVE -> if (_objects.isEmpty()) {
                resultChangeSet = thisChangeSet
            } else {
                resultChangeSet = DefaultAdditiveChangeSet()
                blockToUse = blockToUse.let { currentBlockToUse ->
                    {
                        try {
                            currentBlockToUse()
                        } finally {
                            resultChangeSet += thisChangeSet
                        }
                    }
                }
                blockToUse = blockToUse.let { currentBlockToUse ->
                    _objects.fold(currentBlockToUse) { prev, obj ->
                        { resultChangeSet += obj.recordChanges(depth, mode) { prev() } }
                    }
                }
            }
        }
        try {
            blockToUse()
            return resultChangeSet
        } finally {
            this.changeSet = null
            this.recordOnly = false
        }
    }
}