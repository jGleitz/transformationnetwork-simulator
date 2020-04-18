package de.joshuagleitze.transformationnetwork.models.guestlist

import de.joshuagleitze.transformationnetwork.changerecording.factory.DefaultModelObject
import de.joshuagleitze.transformationnetwork.changerecording.factory.metaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.AnyModelObjectIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.BaseMetaclass
import de.joshuagleitze.transformationnetwork.metametamodel.Metaclass as MetametamodelMetaclass

class Guest(identity: AnyModelObjectIdentity? = null) : DefaultModelObject<Guest>(Metaclass, identity) {
    var name by attributeAccess(Metaclass.Attributes.name)
    var age by attributeAccess(Metaclass.Attributes.age)

    object Metaclass : BaseMetaclass<Guest>() {
        override val name: String get() = "Guest"
        override val ownAttributes = setOf(
            Attributes.name,
            Attributes.age
        )
        override val superClasses: Set<MetametamodelMetaclass<*>> get() = emptySet()

        override fun createNew(identity: AnyModelObjectIdentity?) = Guest(identity)

        object Attributes {
            val name = metaAttribute<String>("name")
            val age = metaAttribute<Int>("age")
        }
    }
}
