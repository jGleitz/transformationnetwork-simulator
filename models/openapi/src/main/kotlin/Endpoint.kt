package de.joshuagleitze.transformationnetwork.models.openapi

import de.joshuagleitze.transformationnetwork.changerecording.factory.DefaultModelObject
import de.joshuagleitze.transformationnetwork.changerecording.factory.metaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.AnyMetaclass
import de.joshuagleitze.transformationnetwork.metametamodel.AnyModelObjectIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.BaseMetaclass
import de.joshuagleitze.transformationnetwork.metametamodel.MetaAttribute

class Endpoint(identity: AnyModelObjectIdentity? = null) : DefaultModelObject<Endpoint>(Metaclass, identity) {
    var method by attributeAccess(Metaclass.Attributes.method)
    var path by attributeAccess(Metaclass.Attributes.path)

    object Metaclass : BaseMetaclass<Endpoint>() {
        override val name: String get() = "Endpoint"
        override val ownAttributes: Set<MetaAttribute<*>> get() = setOf(Attributes.method, Attributes.path)
        override val superClasses: Set<AnyMetaclass> get() = emptySet()

        override fun createNew(identity: AnyModelObjectIdentity?) = Endpoint(identity)

        object Attributes {
            val method = metaAttribute<String>("method")
            val path = metaAttribute<String>("path")
        }
    }
}
