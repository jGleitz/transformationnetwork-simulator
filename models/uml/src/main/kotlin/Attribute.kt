package de.joshuagleitze.transformationnetwork.models.uml

import de.joshuagleitze.transformationnetwork.changerecording.factory.DefaultModelObject
import de.joshuagleitze.transformationnetwork.changerecording.factory.metaAttribute
import de.joshuagleitze.transformationnetwork.changerecording.factory.metaReference
import de.joshuagleitze.transformationnetwork.metametamodel.BaseMetaclass
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObjectIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.Metaclass as MetametamodelMetaclass

class Attribute(identity: ModelObjectIdentity? = null) : DefaultModelObject(Metaclass, identity) {
    var name by attributeAccess(Metaclass.Attributes.name)
    var type by attributeAccess(Metaclass.Attributes.type)

    object Metaclass : BaseMetaclass() {
        override val name get() = "Attribute"
        override val ownAttributes get() = setOf(Attributes.name, Attributes.type)
        override val superClasses: Set<MetametamodelMetaclass> get() = emptySet()

        override fun createNew(identity: ModelObjectIdentity?) = Attribute(identity)

        object Attributes {
            val name = metaAttribute<String>("name")
            val type = metaReference("type", Interface.Metaclass)
        }
    }

    override fun copy() = Attribute().also { import(this) }
}