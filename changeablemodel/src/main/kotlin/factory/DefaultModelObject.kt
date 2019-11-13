package de.joshuagleitze.transformationnetwork.changeablemodel.factory

import de.joshuagleitze.transformationnetwork.changeablemodel.ChangeRecording
import de.joshuagleitze.transformationnetwork.changeablemodel.ChangeRecordingModel
import de.joshuagleitze.transformationnetwork.changeablemodel.ChangeRecordingModelObject
import de.joshuagleitze.transformationnetwork.changemetamodel.AttributeSetChange
import de.joshuagleitze.transformationnetwork.changemetamodel.DefaultAdditiveChangeSet
import de.joshuagleitze.transformationnetwork.metametamodel.MetaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.MetaAttributeMap
import de.joshuagleitze.transformationnetwork.metametamodel.Metaclass
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class DefaultModelObject(final override val metaclass: Metaclass) : ChangeRecordingModelObject {
    override var model: ChangeRecordingModel? = null
    private val _attributes = AttributeMap(metaclass)
    override val attributes: MetaAttributeMap get() = _attributes

    protected fun <T : Any> attributeAccess(attribute: MetaAttribute<T>) = _attributes.attributeAccess(attribute)

    override fun getLastChanges() = _attributes.getLastChanges()

    override fun resetLastChanges() = _attributes.resetLastChanges()

    private inner class AttributeMap(val metaclass: Metaclass) : MetaAttributeMap, ChangeRecording {
        private val map = HashMap<MetaAttribute<*>, Any>()
        internal var changeSet = DefaultAdditiveChangeSet()

        override fun getLastChanges() = changeSet.copy()

        override fun resetLastChanges() {
            changeSet = DefaultAdditiveChangeSet()
        }

        override fun <T : Any> set(attribute: MetaAttribute<T>, value: T?) {
            checkIsMember(attribute)
            setChecked(attribute, value)
        }

        internal fun <T : Any> setChecked(attribute: MetaAttribute<T>, value: T?) {
            this@DefaultModelObject.model?.let { model ->
                changeSet.add(AttributeSetChange(model, this@DefaultModelObject, attribute, this.getChecked(attribute)))
            }
            if (value == null) map.remove(attribute)
            else {
                check(attribute.elementType.isInstance(value)) { "value '$value' for $attribute has wrong type ${value::class} instead of expected ${attribute.elementType}!" }
                map[attribute] = value
            }
        }

        override fun <T : Any> get(attribute: MetaAttribute<T>): T? {
            checkIsMember(attribute)
            return getChecked(attribute)
        }

        @Suppress("UNCHECKED_CAST")
        internal fun <T : Any> getChecked(attribute: MetaAttribute<T>) = map[attribute] as T?

        private fun checkIsMember(attribute: MetaAttribute<*>) =
            check(metaclass.attributes.contains(attribute)) { "$attribute is not an attribute of $metaclass!" }

        internal fun <T : Any> attributeAccess(attribute: MetaAttribute<T>): ReadWriteProperty<DefaultModelObject, T?> {
            checkIsMember(attribute)
            return AttributeAccess(attribute)
        }
    }

    private class AttributeAccess<T : Any>(val attribute: MetaAttribute<T>) :
        ReadWriteProperty<DefaultModelObject, T?> {
        override fun getValue(thisRef: DefaultModelObject, property: KProperty<*>) =
            thisRef._attributes.getChecked(attribute)

        override fun setValue(thisRef: DefaultModelObject, property: KProperty<*>, value: T?) =
            thisRef._attributes.setChecked(attribute, value)
    }
}