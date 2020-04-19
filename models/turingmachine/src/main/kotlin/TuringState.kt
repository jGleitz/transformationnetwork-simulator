package de.joshuagleitze.transformationnetwork.models.turingmachine

import de.joshuagleitze.transformationnetwork.changerecording.factory.DefaultModelObject
import de.joshuagleitze.transformationnetwork.changerecording.factory.metaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.AnyMetaclass
import de.joshuagleitze.transformationnetwork.metametamodel.AnyModelObjectIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.BaseMetaclass

class TuringState(identity: AnyModelObjectIdentity? = null) : DefaultModelObject<TuringState>(Metaclass, identity) {
    var timestamp by attributeAccess(Metaclass.Attributes.timestamp)
    var band by attributeAccess(Metaclass.Attributes.band)
    var bandPosition by attributeAccess(Metaclass.Attributes.bandPosition)

    object Metaclass : BaseMetaclass<TuringState>() {
        override val name: String get() = "Turing State"
        override val ownAttributes = setOf(
            Attributes.timestamp,
            Attributes.band,
            Attributes.bandPosition
        )
        override val superClasses: Set<AnyMetaclass> get() = emptySet()

        override fun createNew(identity: AnyModelObjectIdentity?) = TuringState(identity)

        object Attributes {
            val timestamp = metaAttribute<Int>("timestamp")
            val band = metaAttribute<String>("band")
            val bandPosition = metaAttribute<Int>("band position")
        }
    }
}
