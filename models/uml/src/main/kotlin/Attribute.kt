package de.joshuagleitze.transformationnetwork.models.uml

import de.joshuagleitze.transformationnetwork.changerecording.factory.DefaultModelObject
import de.joshuagleitze.transformationnetwork.changerecording.factory.metaAttribute
import de.joshuagleitze.transformationnetwork.changerecording.factory.metaReference
import de.joshuagleitze.transformationnetwork.metametamodel.AnyMetaclass
import de.joshuagleitze.transformationnetwork.metametamodel.AnyModelObjectIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.BaseMetaclass

class Attribute(identity: AnyModelObjectIdentity? = null) : DefaultModelObject<Attribute>(Metaclass, identity) {
    var name by attributeAccess(Metaclass.Attributes.name)
    var type by attributeAccess(Metaclass.Attributes.type)

    object Metaclass : BaseMetaclass<Attribute>() {
        override val name get() = "Attribute"
        override val ownAttributes get() = setOf(Attributes.name, Attributes.type)
        override val superClasses: Set<AnyMetaclass> get() = emptySet()

        override fun createNew(identity: AnyModelObjectIdentity?) = Attribute(identity)

        object Attributes {
            val name = metaAttribute<String>("name")
            val type = metaReference("type", Interface.Metaclass)
        }
    }
}
