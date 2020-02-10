package de.joshuagleitze.transformationnetwork.models.java

import de.joshuagleitze.transformationnetwork.metametamodel.AbstractMetamodel

object JavaMetamodel : AbstractMetamodel() {
    override val name: String get() = "Java"

    override val classes get() = setOf(Interface.Metaclass, Class.Metaclass, Method.Metaclass)
}