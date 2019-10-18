package de.joshuagleitze.transformationnetwork.models.persons

import de.joshuagleitze.transformationnetwork.models.persons.metamodel.Person
import de.joshuagleitze.transformationnetwork.models.persons.metamodel.PersonsMetamodel
import metamodelling.factory.model
import kotlin.js.Date

val Martin = Person.with {
    firstName = "Martin"
    lastName = "Mustermann"
    birthDate = Date(1991, 4, 6)
}

val NicePersons = PersonsMetamodel.model("Nice Persons", Martin)

