package examplemodels.persons

import model.factory.DefaultModelObject

class Person : DefaultModelObject(PersonMetaclass) {
    var firstName by attributeAccess(PersonMetaclass.Attributes.firstName)
    var lastName by attributeAccess(PersonMetaclass.Attributes.lastName)
    var birthDate by attributeAccess(PersonMetaclass.Attributes.birthDate)
}