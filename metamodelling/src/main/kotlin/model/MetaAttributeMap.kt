package model

import de.joshuagleitze.transformationnetwork.model.MetaAttribute

interface MetaAttributeMap {
    operator fun <T : Any> get(attribute: MetaAttribute<T>): T?
    operator fun <T : Any> set(attribute: MetaAttribute<T>, value: T?)
}