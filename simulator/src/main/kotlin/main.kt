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
import de.joshuagleitze.transformationnetwork.models.turingmachine.TuringMachineMetamodel
import de.joshuagleitze.transformationnetwork.models.turingmachine.TuringState.Metaclass.Attributes.timestamp
import de.joshuagleitze.transformationnetwork.models.turingmachine.withInitialState
import de.joshuagleitze.transformationnetwork.models.uml.Interface
import de.joshuagleitze.transformationnetwork.models.uml.Method
import de.joshuagleitze.transformationnetwork.models.uml.UmlMetamodel
import de.joshuagleitze.transformationnetwork.network.strategies.OncePerTransformation
import de.joshuagleitze.transformationnetwork.network.strategies.StepByStep
import de.joshuagleitze.transformationnetwork.network.strategies.UnboundedPropagation
import de.joshuagleitze.transformationnetwork.simulator.components.simulator.TransformationSimulator
import de.joshuagleitze.transformationnetwork.simulator.data.scenario.SimulatorScenario
import de.joshuagleitze.transformationnetwork.simulator.data.scenario.at
import de.joshuagleitze.transformationnetwork.simulator.data.strategy.describe
import de.joshuagleitze.transformationnetwork.simulator.styles.globalStyleSheet
import de.joshuagleitze.transformationnetwork.simulator.util.geometry.x
import de.joshuagleitze.transformationnetwork.transformations.Java2OpenApiTransformation
import de.joshuagleitze.transformationnetwork.transformations.Persons2GuestsTransformation
import de.joshuagleitze.transformationnetwork.transformations.Uml2JavaTransformation
import de.joshuagleitze.transformationnetwork.transformations.busybeaver3.Q0Q1
import de.joshuagleitze.transformationnetwork.transformations.busybeaver3.Q0Qf
import de.joshuagleitze.transformationnetwork.transformations.busybeaver3.Q1Q2
import de.joshuagleitze.transformationnetwork.transformations.busybeaver3.Q2Q0
import kotlinext.js.invoke
import react.dom.render
import styled.createGlobalStyle
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
        }(),
        {
            val q0 = TuringMachineMetamodel.model("q0").withInitialState("0")
            val q1 = TuringMachineMetamodel.model("q1").withInitialState("0")
            val q2 = TuringMachineMetamodel.model("q2").withInitialState("0")
            val qf = TuringMachineMetamodel.model("qf").withInitialState("0")
            SimulatorScenario(
                "3-State Busy Beaver",
                models = listOf(
                    q0 at (1 x 1),
                    q1 at (1 x 2),
                    qf at (2 x 1),
                    q2 at (2 x 2)
                ),
                transformations = setOf(
                    Q0Q1.create(q0, q1),
                    Q1Q2.create(q1, q2),
                    Q2Q0.create(q2, q0),
                    Q0Qf.create(q0, qf)
                ),
                changes = listOf(
                    changeSetOf(
                        AttributeSetChange(q0, q0.objects.first(), timestamp, 1)
                    )
                )
            )
        }()
    )
}

private val strategies = listOf(
    UnboundedPropagation().describe(name = "Unbounded Propagation"),
    StepByStep().describe(name = "Step by Step"),
    OncePerTransformation().describe(name = "Once per Transformation")
)

fun main() {
    window.onload = {
        val root = document.getElementById("root") ?: throw IllegalStateException("Cannot find the root!")
        createGlobalStyle(globalStyleSheet.toString())

        render(root) {
            TransformationSimulator(scenarios, strategies)
        }
    }
}
