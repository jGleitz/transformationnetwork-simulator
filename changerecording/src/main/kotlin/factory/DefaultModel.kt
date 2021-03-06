package de.joshuagleitze.transformationnetwork.changerecording.factory

import de.joshuagleitze.transformationnetwork.changemetamodel.AdditionChange
import de.joshuagleitze.transformationnetwork.changemetamodel.AnyModelObjectChange
import de.joshuagleitze.transformationnetwork.changemetamodel.DeletionChange
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.AdditiveChangeSet
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.ChangeSet
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.DefaultAdditiveChangeSet
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.ModelSpecificAdditiveChangeSet
import de.joshuagleitze.transformationnetwork.changerecording.AnyChangeRecordingModelObject
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecording
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecording.ApplyMode.RECORD_ONLY
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecording.RecordDepth.DIRECT_ONLY
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecording.RecordDepth.RECURSIVE
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecordingModel
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecordingModelObject
import de.joshuagleitze.transformationnetwork.metametamodel.AnyModelObject
import de.joshuagleitze.transformationnetwork.metametamodel.AnyModelObjectIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.Metamodel
import de.joshuagleitze.transformationnetwork.metametamodel.ModelIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObject
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObjectIdentifier
import de.joshuagleitze.transformationnetwork.metametamodel.newIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.sameValuesAs
import de.joshuagleitze.transformationnetwork.publishsubscribe.Observable
import de.joshuagleitze.transformationnetwork.publishsubscribe.PublishingObservable

fun Metamodel.model(name: String, vararg objects: AnyChangeRecordingModelObject): ChangeRecordingModel {
    val metamodel = this
    objects.forEach {
        check(metamodel.classes.contains(it.metaclass)) { "$it’s metaclass ${it.metaclass} does not belong to $metamodel!" }
    }
    return DefaultModel(name, metamodel, objects.asIterable())
}

internal class DefaultModel private constructor(
    override val name: String,
    objects: Iterable<AnyChangeRecordingModelObject>,
    override val identity: ModelIdentity
) : ChangeRecordingModel {
    override val metamodel: Metamodel get() = identity.metamodel
    private var changeRecording: ChangeRecordingProcess? = null
    private val _objects: MutableSet<AnyChangeRecordingModelObject> = LinkedHashSet()
    override val objects: Set<AnyChangeRecordingModelObject> get() = _objects
    private val _changes = PublishingObservable<AnyModelObjectChange>()
    override val directChanges: Observable<AnyModelObjectChange> get() = _changes

    private val recordOnly get() = this.changeRecording?.mode == RECORD_ONLY

    constructor(name: String, metamodel: Metamodel, objects: Iterable<AnyChangeRecordingModelObject>)
        : this(name, objects, newIdentity(metamodel))

    init {
        objects.forEach { modelObject ->
            this += modelObject
        }
    }

    override fun plusAssign(modelObject: AnyModelObject) {
        check(metamodel.classes.contains(modelObject.metaclass)) { "$modelObject’s metaclass '${modelObject.metaclass}' does not belong to this model’s metamodel '$metamodel'!" }
        if (modelObject is DefaultModelObject<*> && !recordOnly) {
            modelObject.internalModel = this
        }
        @Suppress("UNCHECKED_CAST")
        modelObject as ChangeRecordingModelObject<Nothing>
        val isChange = if (!recordOnly) _objects.add(modelObject) else !_objects.contains(modelObject)
        if (isChange) recordAndPublish(AdditionChange(this.identity, modelObject.metaclass, modelObject.identity))
        val changeRecording = this.changeRecording
        if (changeRecording?.depth == RECURSIVE) {
            if (modelObject is DefaultModelObject<*>) {
                val objectChanges = modelObject.recordUntil(changeRecording.mode, changeRecording.endObservable)
                changeRecording.addAdditionalChanges(objectChanges)
            }
        }
    }

    override fun minusAssign(modelObject: AnyModelObject) {
        check(objects.contains(modelObject)) { "this model does not contain the model object '$modelObject'!" }
        if (modelObject is DefaultModelObject && !recordOnly) {
            modelObject.internalModel = null
        }
        modelObject as ChangeRecordingModelObject
        val isChange = if (!recordOnly) _objects.remove(modelObject) else _objects.contains(modelObject)
        if (isChange) recordAndPublish(DeletionChange(this.identity, sameValuesAs(modelObject)))
    }

    override fun minusAssign(modelObject: AnyModelObjectIdentity) = minusAssign(
        checkNotNull(objects.find { modelObject.identifies(it) }) {
            { "this model does not contain a model object identified by '$modelObject'!" }
        }
    )

    private fun recordAndPublish(change: AnyModelObjectChange) {
        changeRecording?.set?.add(change)
        if (!recordOnly) {
            _changes.publishIfChanged(change)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <O : ModelObject<O>> getObject(identifier: ModelObjectIdentifier<O>): O? =
        _objects.find { identifier.matches(it) } as O?

    override fun copy() =
        DefaultModel(
            this.name,
            this._objects.map { it.copy(it.identity) },
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
            check(this.changeRecording == null) { "There is already another change recording in progress for $this!" }
            this.changeRecording = ChangeRecordingProcess(thisChangeSet, mode, depth)
        }
        var blockToUse = block
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
            for (additionalChanges in this.changeRecording!!.additionalSets) resultChangeSet += additionalChanges
            return resultChangeSet
        } finally {
            this.changeRecording!!.endObservable.publish(Unit)
            this.changeRecording = null
        }
    }

    private class ChangeRecordingProcess(
        internal val set: AdditiveChangeSet,
        internal val mode: ChangeRecording.ApplyMode,
        internal val depth: ChangeRecording.RecordDepth
    ) {
        internal val endObservable = PublishingObservable<Unit>()
        private val _additionalSets = ArrayList<ChangeSet>()
        internal val additionalSets get() = _additionalSets

        internal fun addAdditionalChanges(changes: ChangeSet) {
            _additionalSets += changes
        }
    }
}
