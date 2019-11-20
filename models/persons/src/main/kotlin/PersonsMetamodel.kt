package de.joshuagleitze.transformationnetwork.models.persons

import de.joshuagleitze.transformationnetwork.changerecording.factory.metaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.AbstractMetamodel
import de.joshuagleitze.transformationnetwork.metametamodel.Metaclass
import kotlin.js.Date

object PersonsMetamodel : AbstractMetamodel() {
    override val name: String get() = "Persons"

    override val classes get() = setOf(PersonMetaclass)
}

object PersonMetaclass : Metaclass {
    override val name: String get() = "Person"
    override val attributes = setOf(
        Attributes.firstName,
        Attributes.lastName,
        Attributes.birthDate
    )

    object Attributes {
        val firstName = metaAttribute<String>("first name")
        val lastName = metaAttribute<String>("last name")
        val birthDate = metaAttribute<Date>("birthdate")
    }
}