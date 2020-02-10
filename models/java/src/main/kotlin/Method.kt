package de.joshuagleitze.transformationnetwork.models.java

import de.joshuagleitze.transformationnetwork.changerecording.factory.DefaultModelObject
import de.joshuagleitze.transformationnetwork.changerecording.factory.listMetaAttribute
import de.joshuagleitze.transformationnetwork.changerecording.factory.metaAttribute
import de.joshuagleitze.transformationnetwork.changerecording.factory.metaReference
import de.joshuagleitze.transformationnetwork.metametamodel.BaseMetaclass
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObjectIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.Metaclass as MetametamodelMetaclass

class Method(identity: ModelObjectIdentity? = null) : DefaultModelObject(Metaclass, identity) {
    var name by attributeAccess(Metaclass.Attributes.name)
    var parameters by attributeAccess(Metaclass.Attributes.parameters)
    var visibility by attributeAccess(Metaclass.Attributes.visiblity)
    var modifiers by attributeAccess(Metaclass.Attributes.modifiers)

    object Metaclass : BaseMetaclass() {
        override val name get() = "Method"
        override val ownAttributes
            get() = setOf(
                Attributes.name,
                Attributes.parameters,
                Attributes.visiblity,
                Attributes.modifiers
            )
        override val superClasses: Set<MetametamodelMetaclass> get() = emptySet()

        override fun createNew(identity: ModelObjectIdentity?) = Method(identity)

        object Attributes {
            val name = metaAttribute<String>("name")
            val parameters = listMetaAttribute<String>("parameters")
            val visiblity = metaAttribute<String>("visibility")
            val modifiers = listMetaAttribute<String>("modifiers")
            val returnType = metaReference("return type", Interface.Metaclass)
        }
    }

    override fun copy() = Method().also { import(this) }
}