package model.factory

import de.joshuagleitze.transformationnetwork.model.MetaAttribute
import de.joshuagleitze.transformationnetwork.model.Metaclass
import de.joshuagleitze.transformationnetwork.model.ModelObject
import model.MetaAttributeMap
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class DefaultModelObject(override val metaclass: Metaclass) : ModelObject {
    internal val _attributes = AttributeMap(metaclass)
    override val attributes: MetaAttributeMap get() = _attributes

    protected fun <T : Any> attributeAccess(attribute: MetaAttribute<T>) = _attributes.attributeAccess(attribute)
}

internal class AttributeMap(val metaclass: Metaclass) : MetaAttributeMap {
    private val map = HashMap<MetaAttribute<*>, Any>()

    override fun <T : Any> set(attribute: MetaAttribute<T>, value: T?) {
        checkIsMember(attribute)
        setChecked(attribute, value)
    }

    private fun <T : Any> setChecked(attribute: MetaAttribute<T>, value: T?) {
        if (value == null) map.remove(attribute)
        else {
            check(attribute.elementType.isInstance(value)) { "value '$value' for $attribute has wrong type ${value::class} instead of expected ${attribute.elementType}!" }
            map[attribute] = value
        }
    }

    override fun <T : Any> get(attribute: MetaAttribute<T>): T? {
        checkIsMember(attribute)
        @Suppress("UNCHECKED_CAST")
        return map[attribute] as T?
    }

    private fun checkIsMember(attribute: MetaAttribute<*>) =
        check(metaclass.attributes.contains(attribute)) { "$attribute is not an attribute of $metaclass!" }

    internal fun <T : Any> attributeAccess(attribute: MetaAttribute<T>): ReadWriteProperty<DefaultModelObject, T?> {
        checkIsMember(attribute)
        return AttributeAccess(attribute)
    }

    private class AttributeAccess<T : Any>(val attribute: MetaAttribute<T>) :
        ReadWriteProperty<DefaultModelObject, T?> {
        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: DefaultModelObject, property: KProperty<*>) =
            thisRef._attributes.map[attribute] as T?

        override fun setValue(thisRef: DefaultModelObject, property: KProperty<*>, value: T?) =
            thisRef._attributes.setChecked(attribute, value)
    }
}