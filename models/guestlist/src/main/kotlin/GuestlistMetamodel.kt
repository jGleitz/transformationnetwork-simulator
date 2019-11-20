package de.joshuagleitze.transformationnetwork.models.guestlist

import de.joshuagleitze.transformationnetwork.changerecording.factory.metaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.AbstractMetamodel
import de.joshuagleitze.transformationnetwork.metametamodel.Metaclass

object GuestlistMetamodel : AbstractMetamodel() {
    override val name: String get() = "Guest List"

    override val classes get() = setOf(GuestMetaclass)
}

object GuestMetaclass : Metaclass {
    override val name: String get() = "Guest"
    override val attributes = setOf(
        Attributes.name,
        Attributes.age
    )

    object Attributes {
        val name = metaAttribute<String>("name")
        val age = metaAttribute<Int>("age")
    }
}