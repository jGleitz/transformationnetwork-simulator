package examplemodels.persons

import de.joshuagleitze.transformationnetwork.model.Metaclass
import de.joshuagleitze.transformationnetwork.model.Metamodel
import model.factory.metaAttribute
import kotlin.js.Date

object PersonsMetamodel : Metamodel {
    override val classes get() = setOf(PersonMetaclass)
}

object PersonMetaclass : Metaclass {
    override val name: String get() = "Person"
    override val attributes = setOf(Attributes.firstName, Attributes.lastName, Attributes.birthDate)

    object Attributes {
        val firstName = metaAttribute<String>("first name")
        val lastName = metaAttribute<String>("last name")
        val birthDate = metaAttribute<Date>("birthdate")
    }
}