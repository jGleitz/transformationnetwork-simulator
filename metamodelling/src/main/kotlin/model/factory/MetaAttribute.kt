package model.factory

import de.joshuagleitze.transformationnetwork.model.MetaAttribute
import kotlin.reflect.KClass

inline fun <reified T : Any> metaAttribute(name: String): MetaAttribute<T> = MetaAttributeImpl(name, T::class)

@PublishedApi
internal class MetaAttributeImpl<T : Any>(override val name: String, override val elementType: KClass<T>) :
    MetaAttribute<T>