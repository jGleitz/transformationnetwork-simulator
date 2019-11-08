package de.joshuagleitze.transformationnetwork.metametamodel

import kotlin.reflect.KClass

interface MetaAttribute<T : Any> {
    val name: String
    val elementType: KClass<T>
}