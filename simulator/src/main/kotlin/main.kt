package de.joshuagleitze.transformationnetwork.simulator

import de.joshuagleitze.transformationnetwork.changemetamodel.AttributeSetChange
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.changeSetOf
import de.joshuagleitze.transformationnetwork.changerecording.changesForAdding
import de.joshuagleitze.transformationnetwork.changerecording.factory.model
import de.joshuagleitze.transformationnetwork.metametamodel.byIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.search
import de.joshuagleitze.transformationnetwork.models.guestlist.Guest
import de.joshuagleitze.transformationnetwork.models.guestlist.Guest.Metaclass.Attributes.age
import de.joshuagleitze.transformationnetwork.models.guestlist.Guest.Metaclass.Attributes.name
import de.joshuagleitze.transformationnetwork.models.guestlist.GuestlistMetamodel
import de.joshuagleitze.transformationnetwork.models.java.JavaMetamodel
import de.joshuagleitze.transformationnetwork.models.openapi.OpenApiMetamodel
import de.joshuagleitze.transformationnetwork.models.persons.Person
import de.joshuagleitze.transformationnetwork.models.persons.Person.Metaclass.Attributes.birthDate
import de.joshuagleitze.transformationnetwork.models.persons.Person.Metaclass.Attributes.firstName
import de.joshuagleitze.transformationnetwork.models.persons.Person.Metaclass.Attributes.lastName
import de.joshuagleitze.transformationnetwork.models.persons.PersonsMetamodel
import de.joshuagleitze.transformationnetwork.models.uml.Interface
import de.joshuagleitze.transformationnetwork.models.uml.Method
import de.joshuagleitze.transformationnetwork.models.uml.UmlMetamodel
import de.joshuagleitze.transformationnetwork.network.strategies.UnboundedPropagation
import de.joshuagleitze.transformationnetwork.simulator.components.simulator.TransformationSimulator
import de.joshuagleitze.transformationnetwork.simulator.data.scenario.SimulatorScenario
import de.joshuagleitze.transformationnetwork.simulator.data.scenario.at
import de.joshuagleitze.transformationnetwork.simulator.data.scenario.x
import de.joshuagleitze.transformationnetwork.simulator.data.strategy.describe
import de.joshuagleitze.transformationnetwork.simulator.styles.globalStyleSheet
import de.joshuagleitze.transformationnetwork.transformations.Java2OpenApiTransformation
import de.joshuagleitze.transformationnetwork.transformations.Persons2GuestsTransformation
import de.joshuagleitze.transformationnetwork.transformations.Uml2JavaTransformation
import react.dom.render
import styled.StyledComponents
import styled.injectGlobal
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Date

private val scenarios by lazy {
    listOf(
        {
            val nicePersons = PersonsMetamodel.model("Nice Persons")
            val family = PersonsMetamodel.model("Family")
            val guestlist = GuestlistMetamodel.model("Guest List")
            SimulatorScenario(
                "Persons and Guests",
                models = listOf(
                    nicePersons at (1 x 1),
                    guestlist at (2 x 2),
                    family at (1 x 3)
                ),
                transformations = setOf(
                    Persons2GuestsTransformation.create(nicePersons, guestlist),
                    Persons2GuestsTransformation.create(family, guestlist)
                ),
                changes = listOf(
                    nicePersons.changesForAdding(
                        Person().apply {
                            firstName = "Martin"
                            lastName = "Mustermann"
                            birthDate = Date(1991, 4, 11)
                        }
                    ),
                    changeSetOf(
                        AttributeSetChange(
                            guestlist.identity,
                            search(Guest.Metaclass) { it[name] == "Martin Mustermann" },
                            age, 31
                        ),
                        AttributeSetChange(
                            family.identity,
                            search(Person.Metaclass) { it[firstName] == "Martin" && it[lastName] == "Mustermann" },
                            birthDate, Date(1988, 2, 4)
                        )
                    )
                )
            )
        }(),
        SimulatorScenario(
            "Single Model", listOf(PersonsMetamodel.model("Single") at (1 x 1)), setOf(), listOf()
        ),
        {
            val uml = UmlMetamodel.model("Architektur")
            val beispielMethod = Method().apply {
                name = "getBeispiele"
                parameters = listOf()
            }
            val java = JavaMetamodel.model("Implementierung")
            val openApi = OpenApiMetamodel.model("REST API")
            SimulatorScenario(
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
                            name = "BeispielService"
                            methods = listOf(byIdentity(beispielMethod))
                        })
                    )
                )
            )
        }()
    )
}

private val strategies = listOf(
    UnboundedPropagation().describe(name = "Unbounded Propagation")
)

fun main() {
    window.onload = {
        val root = document.getElementById("root") ?: throw IllegalStateException("Cannot find the root!")
        StyledComponents.injectGlobal(globalStyleSheet.toString())

        render(root) {
            TransformationSimulator(scenarios, strategies)
        }
    }
}
