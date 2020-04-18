package de.joshuagleitze.transformationnetwork.models.persons

import de.joshuagleitze.transformationnetwork.changerecording.factory.DefaultModelObject
import de.joshuagleitze.transformationnetwork.changerecording.factory.metaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.AnyMetaclass
import de.joshuagleitze.transformationnetwork.metametamodel.AnyModelObjectIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.BaseMetaclass
import kotlin.js.Date

class Person(identity: AnyModelObjectIdentity? = null) : DefaultModelObject<Person>(Metaclass, identity) {
    var firstName by attributeAccess(Metaclass.Attributes.firstName)
    var lastName by attributeAccess(Metaclass.Attributes.lastName)
    var birthDate by attributeAccess(Metaclass.Attributes.birthDate)

    object Metaclass : BaseMetaclass<Person>() {
        override val name: String get() = "Person"
        override val ownAttributes = setOf(
            Attributes.firstName,
            Attributes.lastName,
            Attributes.birthDate
        )
        override val superClasses: Set<AnyMetaclass> get() = emptySet()

        override fun createNew(identity: AnyModelObjectIdentity?) = Person(identity)

        object Attributes {
            val firstName = metaAttribute<String>("first name")
            val lastName = metaAttribute<String>("last name")
            val birthDate = metaAttribute<Date>("birthdate")
        }
    }
}
