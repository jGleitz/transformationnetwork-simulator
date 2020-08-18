package de.joshuagleitze.transformationnetwork.simulator.scenarios

import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.changeSetOf
import de.joshuagleitze.transformationnetwork.changerecording.changesForAdding
import de.joshuagleitze.transformationnetwork.changerecording.factory.model
import de.joshuagleitze.transformationnetwork.metametamodel.byIdentity
import de.joshuagleitze.transformationnetwork.models.java.JavaMetamodel
import de.joshuagleitze.transformationnetwork.models.openapi.OpenApiMetamodel
import de.joshuagleitze.transformationnetwork.models.uml.Interface
import de.joshuagleitze.transformationnetwork.models.uml.Method
import de.joshuagleitze.transformationnetwork.models.uml.UmlMetamodel
import de.joshuagleitze.transformationnetwork.simulator.data.scenario.SimulatorScenario
import de.joshuagleitze.transformationnetwork.simulator.data.scenario.at
import de.joshuagleitze.transformationnetwork.simulator.util.geometry.x
import de.joshuagleitze.transformationnetwork.transformations.Java2OpenApiTransformation
import de.joshuagleitze.transformationnetwork.transformations.Uml2JavaTransformation

object ObjectOriented {
    fun create(): SimulatorScenario {
        val uml = UmlMetamodel.model("Architecture")
        val beispielMethod = Method().apply {
            name = "getExamples"
            parameters = listOf()
        }
        val java = JavaMetamodel.model("Implementation")
        val openApi = OpenApiMetamodel.model("HTTP API")
        return SimulatorScenario(
            "UML, Java & OpenApi",
            models = listOf(uml at (1 x 1), java at (2 x 1), openApi at (3 x 1)),
            transformations = setOf(
                Uml2JavaTransformation.create(uml, java),
                Java2OpenApiTransformation.create(java, openApi)
            ),
            changes = listOf(
                changeSetOf(
                    uml.changesForAdding(beispielMethod),
                    uml.changesForAdding(Interface().apply {
                        name = "ExampleService"
                        methods = listOf(byIdentity(beispielMethod))
                    })
                )
            )
        )
    }
}
