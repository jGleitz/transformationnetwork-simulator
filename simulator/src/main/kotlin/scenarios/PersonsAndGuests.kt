package de.joshuagleitze.transformationnetwork.simulator.scenarios

import de.joshuagleitze.transformationnetwork.changemetamodel.AttributeSetChange
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.changeSetOf
import de.joshuagleitze.transformationnetwork.changerecording.changesForAdding
import de.joshuagleitze.transformationnetwork.changerecording.factory.model
import de.joshuagleitze.transformationnetwork.metametamodel.search
import de.joshuagleitze.transformationnetwork.models.guestlist.Guest
import de.joshuagleitze.transformationnetwork.models.guestlist.GuestlistMetamodel
import de.joshuagleitze.transformationnetwork.models.persons.Person
import de.joshuagleitze.transformationnetwork.models.persons.PersonsMetamodel
import de.joshuagleitze.transformationnetwork.simulator.data.scenario.SimulatorScenario
import de.joshuagleitze.transformationnetwork.simulator.data.scenario.at
import de.joshuagleitze.transformationnetwork.simulator.util.geometry.x
import de.joshuagleitze.transformationnetwork.transformations.Persons2GuestsTransformation
import kotlin.js.Date

object PersonsAndGuests {
    fun create(): SimulatorScenario {
        val nicePersons = PersonsMetamodel.model("Nice Persons")
        val family = PersonsMetamodel.model("Family")
        val guestlist = GuestlistMetamodel.model("Guest List")
        return SimulatorScenario(
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
                        search(Guest.Metaclass) { it[Guest.Metaclass.Attributes.name] == "Martin Mustermann" },
                        Guest.Metaclass.Attributes.age, 31
                    ),
                    AttributeSetChange(
                        family.identity,
                        search(Person.Metaclass) { it[Person.Metaclass.Attributes.firstName] == "Martin" && it[Person.Metaclass.Attributes.lastName] == "Mustermann" },
                        Person.Metaclass.Attributes.birthDate, Date(1988, 2, 4)
                    )
                )
            )
        )
    }
}
