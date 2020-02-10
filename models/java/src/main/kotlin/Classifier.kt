package de.joshuagleitze.transformationnetwork.models.java

import de.joshuagleitze.transformationnetwork.changerecording.factory.DefaultModelObject
import de.joshuagleitze.transformationnetwork.changerecording.factory.listMetaReference
import de.joshuagleitze.transformationnetwork.changerecording.factory.metaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.BaseMetaclass
import de.joshuagleitze.transformationnetwork.metametamodel.MetaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObjectIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.Metaclass as MetametamodelMetaclass

class Classifier(identity: ModelObjectIdentity? = null) : DefaultModelObject(Metaclass, identity) {
    var name by attributeAccess(Metaclass.Attributes.name)
    var methods by attributeAccess(Metaclass.Attributes.methods)

    object Metaclass : BaseMetaclass() {
        override val name: String get() = "Classifier"
        override val ownAttributes: Set<MetaAttribute<*>> get() = setOf(Attributes.name, Attributes.methods)
        override val superClasses: Set<MetametamodelMetaclass> get() = emptySet()

        override fun createNew(identity: ModelObjectIdentity?) = Classifier(identity)

        object Attributes {
            val name = metaAttribute<String>("name")
            val methods = listMetaReference("methods", Method.Metaclass)
        }
    }

    override fun copy() = Classifier().also { import(this) }
}