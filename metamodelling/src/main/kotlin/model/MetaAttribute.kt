package de.joshuagleitze.transformationnetwork.model

import kotlin.reflect.KClass

interface MetaAttribute<T : Any> {
    val name: String
    val elementType: KClass<T>
}