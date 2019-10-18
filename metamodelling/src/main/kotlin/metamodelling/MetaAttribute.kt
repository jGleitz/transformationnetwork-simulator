package de.joshuagleitze.transformationnetwork.metamodelling

import kotlin.reflect.KClass

interface MetaAttribute<T : Any> {
    val name: String
    val elementType: KClass<T>
}