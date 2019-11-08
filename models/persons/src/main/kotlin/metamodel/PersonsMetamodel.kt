package de.joshuagleitze.transformationnetwork.models.persons.metamodel

import de.joshuagleitze.transformationnetwork.metametamodel.Metaclass
import de.joshuagleitze.transformationnetwork.metametamodel.Metamodel
import de.joshuagleitze.transformationnetwork.metametamodel.factory.metaAttribute
import kotlin.js.Date

object PersonsMetamodel : Metamodel {
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