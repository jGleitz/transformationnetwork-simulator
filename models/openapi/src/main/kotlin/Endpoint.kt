package de.joshuagleitze.transformationnetwork.models.openapi

import de.joshuagleitze.transformationnetwork.changerecording.factory.DefaultModelObject
import de.joshuagleitze.transformationnetwork.changerecording.factory.metaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.BaseMetaclass
import de.joshuagleitze.transformationnetwork.metametamodel.MetaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObjectIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.Metaclass as MetametamodelMetaclass

class Endpoint(identity: ModelObjectIdentity? = null) : DefaultModelObject(Metaclass, identity) {
    var method by attributeAccess(Metaclass.Attributes.method)
    var path by attributeAccess(Metaclass.Attributes.path)

    override fun copy() = Endpoint().also { import(this) }

    object Metaclass : BaseMetaclass() {
        override val name: String get() = "Endpoint"
        override val ownAttributes: Set<MetaAttribute<*>> get() = setOf(Attributes.method, Attributes.path)

        override val superClasses: Set<MetametamodelMetaclass> get() = emptySet()
        override fun createNew(identity: ModelObjectIdentity?) = Endpoint(identity)

        object Attributes {
            val method = metaAttribute<String>("method")
            val path = metaAttribute<String>("path")
        }
    }
}