package de.joshuagleitze.transformationnetwork.models.java

import de.joshuagleitze.transformationnetwork.changerecording.factory.DefaultModelObject
import de.joshuagleitze.transformationnetwork.changerecording.factory.listMetaReference
import de.joshuagleitze.transformationnetwork.changerecording.factory.metaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.AnyMetaclass
import de.joshuagleitze.transformationnetwork.metametamodel.AnyModelObjectIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.BaseMetaclass
import de.joshuagleitze.transformationnetwork.metametamodel.MetaAttribute

class Classifier(identity: AnyModelObjectIdentity? = null) : DefaultModelObject<Classifier>(Metaclass, identity) {
    var name by attributeAccess(Metaclass.Attributes.name)
    var methods by attributeAccess(Metaclass.Attributes.methods)

    object Metaclass : BaseMetaclass<Classifier>() {
        override val name: String get() = "Classifier"
        override val ownAttributes: Set<MetaAttribute<*>> get() = setOf(Attributes.name, Attributes.methods)
        override val superClasses: Set<AnyMetaclass> get() = emptySet()

        override fun createNew(identity: AnyModelObjectIdentity?) = Classifier(identity)

        object Attributes {
            val name = metaAttribute<String>("name")
            val methods = listMetaReference("methods", Method.Metaclass)
        }
    }
}
