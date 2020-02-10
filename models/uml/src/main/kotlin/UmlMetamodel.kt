package de.joshuagleitze.transformationnetwork.models.uml

import de.joshuagleitze.transformationnetwork.metametamodel.AbstractMetamodel

object UmlMetamodel : AbstractMetamodel() {
    override val name: String get() = "UML"

    override val classes get() = setOf(Interface.Metaclass, Class.Metaclass, Method.Metaclass)
}