package de.joshuagleitze.transformationnetwork.models.primitives

import de.joshuagleitze.transformationnetwork.changerecording.factory.DefaultModelObject
import de.joshuagleitze.transformationnetwork.changerecording.factory.metaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.AnyMetaclass
import de.joshuagleitze.transformationnetwork.metametamodel.AnyModelObjectIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.BaseMetaclass

class Word(identity: AnyModelObjectIdentity? = null) : DefaultModelObject<Word>(Metaclass, identity) {
    var value by attributeAccess(Metaclass.Attributes.value)

    object Metaclass : BaseMetaclass<Word>() {
        override val name: String get() = "Word"
        override val ownAttributes = setOf(Attributes.value)
        override val superClasses: Set<AnyMetaclass> get() = emptySet()

        override fun createNew(identity: AnyModelObjectIdentity?) = Word(identity)

        object Attributes {
            val value = metaAttribute<String>("word")
        }
    }
}
