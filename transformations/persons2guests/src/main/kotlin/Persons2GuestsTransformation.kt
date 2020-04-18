package de.joshuagleitze.transformationnetwork.transformations

import de.joshuagleitze.transformationnetwork.changemetamodel.AdditionChange
import de.joshuagleitze.transformationnetwork.changemetamodel.AttributeChange
import de.joshuagleitze.transformationnetwork.changerecording.BaseModelTransformation
import de.joshuagleitze.transformationnetwork.changerecording.BaseModelTransformationType
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecordingModel
import de.joshuagleitze.transformationnetwork.metametamodel.ofType
import de.joshuagleitze.transformationnetwork.models.guestlist.Guest
import de.joshuagleitze.transformationnetwork.models.guestlist.Guest.Metaclass.Attributes.age
import de.joshuagleitze.transformationnetwork.models.guestlist.Guest.Metaclass.Attributes.name
import de.joshuagleitze.transformationnetwork.models.guestlist.GuestlistMetamodel
import de.joshuagleitze.transformationnetwork.models.persons.Person
import de.joshuagleitze.transformationnetwork.models.persons.Person.Metaclass.Attributes.birthDate
import de.joshuagleitze.transformationnetwork.models.persons.Person.Metaclass.Attributes.firstName
import de.joshuagleitze.transformationnetwork.models.persons.Person.Metaclass.Attributes.lastName
import de.joshuagleitze.transformationnetwork.models.persons.PersonsMetamodel
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelCorrespondences.CorrespondenceTag
import kotlin.js.Date

private const val MILLIS_IN_YEAR = 1_000L * 60L * 60L * 24L * 365L

class Persons2GuestsTransformation(val personsModel: ChangeRecordingModel, val guestlistModel: ChangeRecordingModel) :
    BaseModelTransformation() {
    override val leftModel: ChangeRecordingModel get() = personsModel
    override val rightModel: ChangeRecordingModel get() = guestlistModel
    override val type get() = Type

    override fun isConsistent(): Boolean {
        return personsModel.objects.ofType(Person.Metaclass).all { person ->
            val guest = correspondences.getRightCorrespondence(person, PERSON_GUEST)
            guest != null
                    && guest.name == nameFromFirstNameAndLastName(person)
                    && guest.age == ageFromBirthDate(person)
        }
                && guestlistModel.objects.ofType(Guest.Metaclass).all { guest ->
            correspondences.getLeftCorrespondence(guest, PERSON_GUEST) != null
        }
    }

    override fun processChangesChecked(leftSide: TransformationSide, rightSide: TransformationSide) {
        leftSide.propagateDeletionsToOtherSide()
        rightSide.propagateDeletionsToOtherSide()
        leftSide.additions.adding(Person.Metaclass).forEach(::processPersonAddition)
        rightSide.additions.adding(Guest.Metaclass).forEach(::processGuestAddition)
        leftSide.modifications.targetting(Person.Metaclass).forEach(::processPersonModification)
        rightSide.modifications.targetting(Guest.Metaclass).forEach(::processGuestModification)
    }

    private fun processPersonAddition(addition: AdditionChange<Person>) {
        val guest = Guest()
        guestlistModel += guest
        correspondences.addLeftToRightCorrespondence(
            addition.addedObjectIdentity,
            guest.identity,
            PERSON_GUEST
        )
    }

    private fun processGuestAddition(addition: AdditionChange<Guest>) {
        val person = Person()
        personsModel += person
        correspondences.addLeftToRightCorrespondence(
            person.identity,
            addition.addedObjectIdentity,
            PERSON_GUEST
        )
    }

    private fun processPersonModification(modification: AttributeChange<Person>) {
        val person = personsModel.requireObject(modification.targetObject)
        val correspondingGuest = correspondences.requireRightCorrespondence(person, PERSON_GUEST)
        when (modification.targetAttribute) {
            firstName, lastName -> correspondingGuest.name = nameFromFirstNameAndLastName(person)
            birthDate -> correspondingGuest.age = ageFromBirthDate(person)
        }
    }

    private fun processGuestModification(modification: AttributeChange<Guest>) {
        val guest = guestlistModel.requireObject(modification.targetObject)
        val correspondingPerson = correspondences.requireLeftCorrespondence(guest, PERSON_GUEST)
        when (modification.targetAttribute) {
            name -> {
                correspondingPerson.changeIfNot(
                    firstName,
                    guest.name isEqualTo ::nameFromFirstNameAndLastName
                ) { firstNameFromName(guest) }
                correspondingPerson.changeIfNot(
                    lastName,
                    guest.name isEqualTo ::nameFromFirstNameAndLastName
                ) { lastNameFromName(guest) }
            }
            age -> correspondingPerson.changeIfNot(
                birthDate,
                guest.age isEqualTo ::ageFromBirthDate
            ) { birthDateFromAge(guest) }
        }
    }

    private fun nameFromFirstNameAndLastName(person: Person) =
        listOfNotNull(person.firstName, person.lastName).joinToString(" ").nullIfEmpty()

    private fun ageFromBirthDate(person: Person) = person.birthDate?.getTime()?.let { birthSeconds ->
        ((Date.now() - birthSeconds).toLong() / (MILLIS_IN_YEAR)).toInt()
    }

    private fun firstNameFromName(guest: Guest) = guest.name?.let { name ->
        val lastSpace = name.lastIndexOf(' ')
        if (lastSpace >= 0) name.substring(0, lastSpace).trim().nullIfEmpty() else name
    }

    private fun lastNameFromName(guest: Guest) = guest.name?.let { name ->
        val lastSpace = name.lastIndexOf(' ')
        if (lastSpace >= 0) name.substring(lastSpace + 1).trim().nullIfEmpty() else null
    }

    private fun birthDateFromAge(guest: Guest) = guest.age?.let { age ->
        Date(year = Date().getFullYear() - age, month = 0)
    }

    private fun String?.nullIfEmpty() = if (this == "") null else this

    companion object Type : BaseModelTransformationType(PersonsMetamodel, GuestlistMetamodel) {
        override fun createChecked(leftModel: ChangeRecordingModel, rightModel: ChangeRecordingModel) =
            Persons2GuestsTransformation(leftModel, rightModel)

        private val PERSON_GUEST = CorrespondenceTag(Person.Metaclass, Guest.Metaclass)
    }
}
