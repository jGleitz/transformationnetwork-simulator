package de.joshuagleitze.transformationnetwork.models.persons.metamodel

import de.joshuagleitze.transformationnetwork.metametamodel.factory.DefaultModelObject

class Person : DefaultModelObject(PersonMetaclass) {
    var firstName by attributeAccess(PersonMetaclass.Attributes.firstName)
    var lastName by attributeAccess(PersonMetaclass.Attributes.lastName)
    var birthDate by attributeAccess(PersonMetaclass.Attributes.birthDate)

    companion object {
        fun with(block: Person.() -> Unit) = Person().apply(block)
    }
}