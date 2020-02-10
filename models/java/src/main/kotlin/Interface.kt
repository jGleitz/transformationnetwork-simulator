package de.joshuagleitze.transformationnetwork.models.java

import de.joshuagleitze.transformationnetwork.changerecording.factory.DefaultModelObject
import de.joshuagleitze.transformationnetwork.metametamodel.BaseMetaclass
import de.joshuagleitze.transformationnetwork.metametamodel.MetaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObjectIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.Metaclass as MetametamodelMetaclass

class Interface(identity: ModelObjectIdentity? = null) : DefaultModelObject(Metaclass, identity) {
    var name by attributeAccess(Metaclass.Attributes.name)
    var methods by attributeAccess(Metaclass.Attributes.methods)

    object Metaclass : BaseMetaclass() {
        override val name: String get() = "Interface"
        override val ownAttributes: Set<MetaAttribute<*>> get() = emptySet()
        override val superClasses: Set<MetametamodelMetaclass> get() = setOf(Classifier.Metaclass)

        override fun createNew(identity: ModelObjectIdentity?) = Interface(identity)

        object Attributes {
            val name = Classifier.Metaclass.Attributes.name
            val methods = Classifier.Metaclass.Attributes.methods
        }
    }

    override fun copy() = Interface().also { import(this) }
}