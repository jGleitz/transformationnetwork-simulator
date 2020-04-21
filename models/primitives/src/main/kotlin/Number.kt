package de.joshuagleitze.transformationnetwork.models.primitives

import de.joshuagleitze.transformationnetwork.changerecording.factory.DefaultModelObject
import de.joshuagleitze.transformationnetwork.changerecording.factory.metaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.AnyMetaclass
import de.joshuagleitze.transformationnetwork.metametamodel.AnyModelObjectIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.BaseMetaclass

class Number(identity: AnyModelObjectIdentity? = null) : DefaultModelObject<Number>(Metaclass, identity) {
    var value by attributeAccess(Metaclass.Attributes.value)

    object Metaclass : BaseMetaclass<Number>() {
        override val name: String get() = "Number"
        override val ownAttributes = setOf(Attributes.value)
        override val superClasses: Set<AnyMetaclass> get() = emptySet()

        override fun createNew(identity: AnyModelObjectIdentity?) = Number(identity)

        object Attributes {
            val value = metaAttribute<Int>("number")
        }
    }
}
