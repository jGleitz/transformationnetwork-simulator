package de.joshuagleitze.transformationnetwork.models.guestlist

import de.joshuagleitze.transformationnetwork.changerecording.factory.DefaultModelObject
import de.joshuagleitze.transformationnetwork.changerecording.factory.metaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.BaseMetaclass
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObjectIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.Metaclass as MetametamodelMetaclass

class Guest(identity: ModelObjectIdentity? = null) : DefaultModelObject(Metaclass, identity) {
    var name by attributeAccess(Metaclass.Attributes.name)
    var age by attributeAccess(Metaclass.Attributes.age)

    override fun copy() = Guest().also { import(this) }

    object Metaclass : BaseMetaclass() {
        override val name: String get() = "Guest"
        override val ownAttributes = setOf(
            Attributes.name,
            Attributes.age
        )
        override val superClasses: Set<MetametamodelMetaclass> get() = emptySet()

        override fun createNew(identity: ModelObjectIdentity?) = Guest(identity)

        object Attributes {
            val name = metaAttribute<String>("name")
            val age = metaAttribute<Int>("age")
        }
    }
}