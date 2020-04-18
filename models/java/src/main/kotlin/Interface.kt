package de.joshuagleitze.transformationnetwork.models.java

import de.joshuagleitze.transformationnetwork.changerecording.factory.DefaultModelObject
import de.joshuagleitze.transformationnetwork.metametamodel.AnyMetaclass
import de.joshuagleitze.transformationnetwork.metametamodel.AnyModelObjectIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.BaseMetaclass
import de.joshuagleitze.transformationnetwork.metametamodel.MetaAttribute

class Interface(identity: AnyModelObjectIdentity? = null) : DefaultModelObject<Interface>(Metaclass, identity) {
    var name by attributeAccess(Metaclass.Attributes.name)
    var methods by attributeAccess(Metaclass.Attributes.methods)

    object Metaclass : BaseMetaclass<Interface>() {
        override val name: String get() = "Interface"
        override val ownAttributes: Set<MetaAttribute<*>> get() = emptySet()
        override val superClasses: Set<AnyMetaclass> get() = setOf(Classifier.Metaclass)

        override fun createNew(identity: AnyModelObjectIdentity?) = Interface(identity)

        object Attributes {
            val name = Classifier.Metaclass.Attributes.name
            val methods = Classifier.Metaclass.Attributes.methods
        }
    }
}
