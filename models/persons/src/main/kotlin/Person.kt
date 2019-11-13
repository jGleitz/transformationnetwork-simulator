package de.joshuagleitze.transformationnetwork.models.persons

import de.joshuagleitze.transformationnetwork.changeablemodel.factory.DefaultModelObject

class Person : DefaultModelObject(PersonMetaclass) {
    var firstName by attributeAccess(PersonMetaclass.Attributes.firstName)
    var lastName by attributeAccess(PersonMetaclass.Attributes.lastName)
    var birthDate by attributeAccess(PersonMetaclass.Attributes.birthDate)
}