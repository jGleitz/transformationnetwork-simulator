package de.joshuagleitze.transformationnetwork.changerecording.factory

import de.joshuagleitze.transformationnetwork.changemetamodel.AttributeSetChange
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.ChangeSet
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.ObjectSpecificAdditiveChangeSet
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecording
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecording.ApplyMode.RECORD_AND_APPLY
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecording.ApplyMode.RECORD_ONLY
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecordingModel
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecordingModelObject
import de.joshuagleitze.transformationnetwork.metametamodel.MetaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.MetaAttributeMap
import de.joshuagleitze.transformationnetwork.metametamodel.Metaclass
import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObject
import de.joshuagleitze.transformationnetwork.publishsubscribe.Observable
import de.joshuagleitze.transformationnetwork.publishsubscribe.PublishingObservable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class DefaultModelObject(final override val metaclass: Metaclass) : ChangeRecordingModelObject {
    override var model: ChangeRecordingModel? = null
    private val _attributes = AttributeMap()
    override val attributes: MetaAttributeMap get() = _attributes
    private var changeSet: ObjectSpecificAdditiveChangeSet? = null
    private var recordOnly = false
    private val _changes = PublishingObservable<AttributeSetChange<*>>()
    override val directChanges: Observable<AttributeSetChange<*>> get() = _changes

    protected fun <T : Any> attributeAccess(attribute: MetaAttribute<T>) = _attributes.attributeAccess(attribute)

    protected fun import(otherModelObject: DefaultModelObject) {
        this.model = otherModelObject.model
        this.metaclass.attributes.forEach { attribute ->
            this[attribute] = otherModelObject[attribute]
        }
    }

    override fun recordChanges(
        depth: ChangeRecording.RecordDepth,
        mode: ChangeRecording.ApplyMode,
        block: () -> Unit
    ): ChangeSet {
        val resultChangeSet = ObjectSpecificAdditiveChangeSet(this)
        synchronized(this) {
            check(this.changeSet == null) { "There is already another change recording in progress for $this!" }
            this.changeSet = resultChangeSet
        }
        when (mode) {
            RECORD_AND_APPLY -> Unit
            RECORD_ONLY -> this.recordOnly = true
        }
        try {
            block()
            return resultChangeSet
        } finally {
            this.changeSet = null
            this.recordOnly = false
        }
    }

    private inner class AttributeMap : MetaAttributeMap {
        private val map: MutableMap<MetaAttribute<*>, Any> = HashMap()

        override fun <T : Any> set(attribute: MetaAttribute<T>, value: T?) = set(attribute as MetaAttribute<*>, value)

        override fun set(attribute: MetaAttribute<*>, value: Any?) {
            checkIsMember(attribute)
            setChecked(attribute, value)
        }

        internal fun setChecked(attribute: MetaAttribute<*>, value: Any?) {
            check(value == null || attribute.elementType.isInstance(value)) { "value '$value' for $attribute has wrong type ${value!!::class} instead of expected ${attribute.elementType}!" }
            @Suppress("UNCHECKED_CAST")
            addAndPublish { model ->
                AttributeSetChange(
                    model.identity,
                    this@DefaultModelObject,
                    attribute as MetaAttribute<Any>,
                    oldValue = this.getChecked(attribute),
                    newValue = value
                )
            }
            if (!recordOnly) {
                if (value == null) map.remove(attribute)
                else {
                    map[attribute] = value
                }
            }
        }

        override fun <T : Any> get(attribute: MetaAttribute<T>): T? {
            checkIsMember(attribute)
            return getChecked(attribute)
        }

        private fun addAndPublish(changeProducer: (Model) -> AttributeSetChange<*>) {
            model?.let { model ->
                val change = changeProducer(model)
                changeSet?.add(change)
                if (!recordOnly) {
                    _changes.publishIfChanged(change)
                }
            }
        }

        @Suppress("UNCHECKED_CAST")
        internal fun <T : Any> getChecked(attribute: MetaAttribute<T>) = map[attribute] as T?

        private fun checkIsMember(attribute: MetaAttribute<*>) =
            check(metaclass.attributes.contains(attribute)) { "$attribute is not an attribute of $metaclass!" }

        internal fun <T : Any> attributeAccess(attribute: MetaAttribute<T>): ReadWriteProperty<DefaultModelObject, T?> {
            checkIsMember(attribute)
            return AttributeAccess(attribute)
        }

        override fun toString() = map.entries.joinToString(separator = ",") { "${it.key.name}=${it.value}" }
    }

    private class AttributeAccess<T : Any>(val attribute: MetaAttribute<T>) :
        ReadWriteProperty<DefaultModelObject, T?> {
        override fun getValue(thisRef: DefaultModelObject, property: KProperty<*>) =
            thisRef._attributes.getChecked(attribute)

        override fun setValue(thisRef: DefaultModelObject, property: KProperty<*>, value: T?) =
            thisRef._attributes.setChecked(attribute, value)
    }

    override fun toString() = "${metaclass.name}($_attributes)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is ModelObject) return false

        if (other.model != this.model) return false
        if (other.metaclass != this.metaclass) return false
        return this.metaclass.attributes.all { other[it] == this[it] }
    }

    override fun hashCode(): Int {
        var result = metaclass.hashCode()
        result = 31 * result + model.hashCode()
        result = 31 * result + this.metaclass.attributes.sumBy { this[it].hashCode() }
        return result
    }
}