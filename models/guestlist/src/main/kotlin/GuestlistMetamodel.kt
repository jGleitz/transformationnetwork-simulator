package de.joshuagleitze.transformationnetwork.models.guestlist

import de.joshuagleitze.transformationnetwork.metametamodel.AbstractMetamodel

object GuestlistMetamodel : AbstractMetamodel() {
    override val name: String get() = "Guest List"

    override val classes get() = setOf(Guest.Metaclass)
}
