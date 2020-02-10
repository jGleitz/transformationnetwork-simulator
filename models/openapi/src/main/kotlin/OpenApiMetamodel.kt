package de.joshuagleitze.transformationnetwork.models.openapi

import de.joshuagleitze.transformationnetwork.metametamodel.Metaclass
import de.joshuagleitze.transformationnetwork.metametamodel.Metamodel

object OpenApiMetamodel : Metamodel {
    override val name: String get() = "Open API"
    override val classes: Set<Metaclass> get() = setOf(Endpoint.Metaclass)

}