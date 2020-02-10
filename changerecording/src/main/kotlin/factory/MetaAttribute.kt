package de.joshuagleitze.transformationnetwork.changerecording.factory

import de.joshuagleitze.transformationnetwork.metametamodel.MetaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.Metaclass
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObjectIdentifier
import kotlin.reflect.KClass

inline fun <reified T : Any> metaAttribute(name: String): MetaAttribute<T> = MetaAttributeImpl(name, T::class)
inline fun <reified T : Any> listMetaAttribute(name: String): MetaAttribute<List<T>> =
    ListMetaAttributeImpl(metaAttribute(name))

fun metaReference(name: String, metaclass: Metaclass): MetaAttribute<ModelObjectIdentifier> =
    ReferenceImpl(name, metaclass)

fun listMetaReference(name: String, metaclass: Metaclass): MetaAttribute<List<ModelObjectIdentifier>> =
    ListMetaAttributeImpl(metaReference(name, metaclass))

@PublishedApi
internal class MetaAttributeImpl<T : Any>(override val name: String, private val elementType: KClass<T>) :
    MetaAttribute<T> {
    override fun canBeValue(value: Any?) = value == null || elementType.isInstance(value)

    override fun checkCanBeValue(value: Any?) {
        check(this.canBeValue(value)) { "the value '$value' for $this has wrong type ${value!!::class} instead of expected $elementType!" }
    }

    override fun toString() = "$name: ${elementType.simpleName}"
}

internal class ReferenceImpl(name: String, private val metaclass: Metaclass) : MetaAttribute<ModelObjectIdentifier> {
    private val innerAttribute = MetaAttributeImpl(name, ModelObjectIdentifier::class)
    override val name: String get() = innerAttribute.name

    override fun canBeValue(value: Any?) =
        innerAttribute.canBeValue(value) && (value as ModelObjectIdentifier).metaclass == metaclass

    override fun checkCanBeValue(value: Any?) {
        innerAttribute.checkCanBeValue(value)
        check((value as ModelObjectIdentifier).metaclass == metaclass) { "$value is not a $metaclass!" }
    }

    override fun toString() = "$name: $metaclass"
}

@PublishedApi
internal class ListMetaAttributeImpl<T : Any>(private val innerAttribute: MetaAttribute<T>) :
    MetaAttribute<List<T>> {
    override fun canBeValue(value: Any?): Boolean {
        return value != null && value is List<*> && value.all { innerAttribute.canBeValue(it) }
    }

    override fun checkCanBeValue(value: Any?) {
        checkNotNull(value) { "A list attribute cannot be null!" }
        check(value is List<*>) { "The value is not a List!" }
        value.forEach { element ->
            innerAttribute.checkCanBeValue(element)
        }
    }

    override val name: String get() = innerAttribute.name

    override fun toString() = "$innerAttribute[]"
}