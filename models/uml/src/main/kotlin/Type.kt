package de.joshuagleitze.transformationnetwork.models.uml

import de.joshuagleitze.transformationnetwork.changerecording.factory.DefaultModelObject
import de.joshuagleitze.transformationnetwork.changerecording.factory.listMetaReference
import de.joshuagleitze.transformationnetwork.changerecording.factory.metaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.AnyMetaclass
import de.joshuagleitze.transformationnetwork.metametamodel.AnyModelObjectIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.BaseMetaclass
import de.joshuagleitze.transformationnetwork.metametamodel.MetaAttribute

class Type(identity: AnyModelObjectIdentity? = null) : DefaultModelObject<Type>(Metaclass, identity) {
    var name by attributeAccess(Metaclass.Attributes.name)
    var methods by attributeAccess(Metaclass.Attributes.methods)

    object Metaclass : BaseMetaclass<Type>() {
        override val name: String get() = "Type"
        override val ownAttributes: Set<MetaAttribute<*>> get() = setOf(Attributes.name, Attributes.methods)
        override val superClasses: Set<AnyMetaclass> get() = emptySet()

        override fun createNew(identity: AnyModelObjectIdentity?) = Type(identity)

        object Attributes {
            val name = metaAttribute<String>("name")
            val methods = listMetaReference("methods", Method.Metaclass)
        }
    }
}
