package de.joshuagleitze.transformationnetwork.models.persons

import de.joshuagleitze.transformationnetwork.metametamodel.AbstractMetamodel

object PersonsMetamodel : AbstractMetamodel() {
    override val name: String get() = "Persons"

    override val classes get() = setOf(Person.Metaclass)
}