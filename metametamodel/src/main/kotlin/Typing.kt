package de.joshuagleitze.transformationnetwork.metametamodel

fun <O : ModelObject<O>> Iterable<AnyModelObject>.ofType(metaclass: Metaclass<O>) =
    this.mapNotNull { it.optionallyAs(metaclass) }

fun <O : ModelObject<O>> Sequence<AnyModelObject>.ofType(metaclass: Metaclass<O>) =
    this.mapNotNull { it.optionallyAs(metaclass) }

@Suppress("UNCHECKED_CAST")
fun <O : ModelObject<O>> AnyModelObject.optionallyAs(metaclass: Metaclass<O>) =
    if (this.metaclass == metaclass) this as O else null
