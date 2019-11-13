package de.joshuagleitze.transformationnetwork.models.guestlist

import de.joshuagleitze.transformationnetwork.changeablemodel.factory.DefaultModelObject

class Guest : DefaultModelObject(GuestMetaclass) {
    var name by attributeAccess(GuestMetaclass.Attributes.name)
    var age by attributeAccess(GuestMetaclass.Attributes.age)
}