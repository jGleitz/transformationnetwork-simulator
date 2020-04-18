package de.joshuagleitze.transformationnetwork.models.uml

import de.joshuagleitze.transformationnetwork.changerecording.factory.DefaultModelObject
import de.joshuagleitze.transformationnetwork.changerecording.factory.metaReference
import de.joshuagleitze.transformationnetwork.metametamodel.AnyMetaclass
import de.joshuagleitze.transformationnetwork.metametamodel.AnyModelObjectIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.BaseMetaclass
import de.joshuagleitze.transformationnetwork.metametamodel.MetaAttribute

class Class(identity: AnyModelObjectIdentity? = null) : DefaultModelObject<Class>(Metaclass, identity) {
    var name by attributeAccess(Type.Metaclass.Attributes.name)
    var methods by attributeAccess(Metaclass.Attributes.methods)
    var implements by attributeAccess(Metaclass.Attributes.implements)

    object Metaclass : BaseMetaclass<Class>() {
        override val name: String get() = "Class"
        override val ownAttributes: Set<MetaAttribute<*>> get() = setOf(Attributes.implements)
        override val superClasses: Set<AnyMetaclass> get() = setOf(Type.Metaclass)

        override fun createNew(identity: AnyModelObjectIdentity?) = Class(identity)

        object Attributes {
            val name = Type.Metaclass.Attributes.name
            val methods = Type.Metaclass.Attributes.methods
            val implements = metaReference("implements", Interface.Metaclass)
        }
    }
}
