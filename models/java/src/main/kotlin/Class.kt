package de.joshuagleitze.transformationnetwork.models.java

import de.joshuagleitze.transformationnetwork.changerecording.factory.DefaultModelObject
import de.joshuagleitze.transformationnetwork.changerecording.factory.metaReference
import de.joshuagleitze.transformationnetwork.metametamodel.BaseMetaclass
import de.joshuagleitze.transformationnetwork.metametamodel.MetaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObjectIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.Metaclass as MetametamodelMetaclass

class Class(identity: ModelObjectIdentity? = null) : DefaultModelObject(Metaclass, identity) {
    var name by attributeAccess(Metaclass.Attributes.name)
    var methods by attributeAccess(Metaclass.Attributes.methods)
    var implements by attributeAccess(Metaclass.Attributes.implements)

    object Metaclass : BaseMetaclass() {
        override val name: String get() = "Class"
        override val ownAttributes: Set<MetaAttribute<*>> get() = setOf(Attributes.implements)
        override val superClasses: Set<MetametamodelMetaclass> get() = setOf(Classifier.Metaclass)

        override fun createNew(identity: ModelObjectIdentity?) = Class(identity)

        object Attributes {
            val name = Classifier.Metaclass.Attributes.name
            val methods = Classifier.Metaclass.Attributes.methods
            val implements = metaReference("implements", Interface.Metaclass)
        }
    }

    override fun copy() = Class().also { import(this) }
}