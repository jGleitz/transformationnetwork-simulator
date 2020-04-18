package de.joshuagleitze.transformationnetwork.models.openapi

import de.joshuagleitze.transformationnetwork.metametamodel.AnyMetaclass
import de.joshuagleitze.transformationnetwork.metametamodel.Metamodel

object OpenApiMetamodel : Metamodel {
    override val name: String get() = "Open API"
    override val classes: Set<AnyMetaclass> get() = setOf(Endpoint.Metaclass)

}
