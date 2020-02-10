package de.joshuagleitze.transformationnetwork.models.persons

import de.joshuagleitze.transformationnetwork.changerecording.factory.DefaultModelObject
import de.joshuagleitze.transformationnetwork.changerecording.factory.metaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.BaseMetaclass
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObjectIdentity
import kotlin.js.Date
import de.joshuagleitze.transformationnetwork.metametamodel.Metaclass as MetametamodelMetaclass

class Person(identity: ModelObjectIdentity? = null) : DefaultModelObject(Metaclass, identity) {
    var firstName by attributeAccess(Metaclass.Attributes.firstName)
    var lastName by attributeAccess(Metaclass.Attributes.lastName)
    var birthDate by attributeAccess(Metaclass.Attributes.birthDate)

    override fun copy() = Person().also { import(this) }

    object Metaclass : BaseMetaclass() {
        override val name: String get() = "Person"
        override val ownAttributes = setOf(
            Attributes.firstName,
            Attributes.lastName,
            Attributes.birthDate
        )
        override val superClasses: Set<MetametamodelMetaclass> get() = emptySet()

        override fun createNew(identity: ModelObjectIdentity?) = Person(identity)

        object Attributes {
            val firstName = metaAttribute<String>("first name")
            val lastName = metaAttribute<String>("last name")
            val birthDate = metaAttribute<Date>("birthdate")
        }
    }
}