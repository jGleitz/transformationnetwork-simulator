package de.joshuagleitze.transformationnetwork.transformations

import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.ChangeSet
import de.joshuagleitze.transformationnetwork.changerecording.BaseModelTransformation
import de.joshuagleitze.transformationnetwork.changerecording.BaseModelTransformationType
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecordingModel
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObject
import de.joshuagleitze.transformationnetwork.models.guestlist.Guest
import de.joshuagleitze.transformationnetwork.models.guestlist.Guest.Metaclass.Attributes.age
import de.joshuagleitze.transformationnetwork.models.guestlist.Guest.Metaclass.Attributes.name
import de.joshuagleitze.transformationnetwork.models.guestlist.GuestlistMetamodel
import de.joshuagleitze.transformationnetwork.models.persons.Person
import de.joshuagleitze.transformationnetwork.models.persons.Person.Metaclass.Attributes.birthDate
import de.joshuagleitze.transformationnetwork.models.persons.Person.Metaclass.Attributes.firstName
import de.joshuagleitze.transformationnetwork.models.persons.Person.Metaclass.Attributes.lastName
import de.joshuagleitze.transformationnetwork.models.persons.PersonsMetamodel
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation.Side.LEFT
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation.Side.RIGHT
import kotlin.js.Date

private const val MILLIS_IN_YEAR = 1_000L * 60L * 60L * 24L * 365L

class Persons2GuestsTransformation(val personsModel: ChangeRecordingModel, val guestlistModel: ChangeRecordingModel) :
    BaseModelTransformation<Nothing>() {
    override val leftModel: ChangeRecordingModel get() = personsModel
    override val rightModel: ChangeRecordingModel get() = guestlistModel
    override val type get() = Type

    override fun processChangesChecked(leftSide: TransformationSide, rightSide: TransformationSide) {
        leftSide.propagateDeletionsToOtherSide()
        rightSide.propagateDeletionsToOtherSide()
        leftSide.processAdditions()
        rightSide.processAdditions()
        processPersonModifications(leftSide)
        processGuestModifications(rightSide)
    }

    private fun TransformationSide.processAdditions() {
        for (addition in additions) {
            val otherSidePersonLike = when (side) {
                LEFT -> Guest()
                RIGHT -> Person()
            }
            otherSideModel += otherSidePersonLike
            correspondences.addCorrespondence(side, addition.addedObjectIdentity, otherSidePersonLike.identity)
        }
    }

    private fun processPersonModifications(personChanges: ChangeSet) {
        for (modification in personChanges.modifications) {
            val person = personsModel.requireObject(modification.targetObject)
            val correspondingGuest = correspondences.requireRightCorrespondence(person)
            when (modification.targetAttribute) {
                firstName, lastName -> correspondingGuest[name] = nameFromFirstNameAndLastName(person)
                birthDate -> correspondingGuest[age] = ageFromBirthDate(person)
            }
        }
    }

    private fun processGuestModifications(guestChanges: ChangeSet) {
        for (modification in guestChanges.modifications) {
            val guest = guestlistModel.requireObject(modification.targetObject)
            val correspondingPerson = correspondences.requireLeftCorrespondence(guest)
            when (modification.targetAttribute) {
                name -> {
                    correspondingPerson.changeIfNot(
                        firstName,
                        guest[name] isEqualTo ::nameFromFirstNameAndLastName
                    ) { firstNameFromName(guest) }
                    correspondingPerson.changeIfNot(
                        lastName,
                        guest[name] isEqualTo ::nameFromFirstNameAndLastName
                    ) { lastNameFromName(guest) }
                }
                age -> correspondingPerson.changeIfNot(
                    birthDate,
                    guest[age] isEqualTo ::ageFromBirthDate
                ) { birthDateFromAge(guest) }
            }
        }
    }

    private fun nameFromFirstNameAndLastName(person: ModelObject) =
        listOfNotNull(person[firstName], person[lastName]).joinToString(" ").nullIfEmpty()

    private fun ageFromBirthDate(person: ModelObject) = person[birthDate]?.getTime()?.let { birthSeconds ->
        ((Date.now() - birthSeconds).toLong() / (MILLIS_IN_YEAR)).toInt()
    }

    private fun firstNameFromName(guest: ModelObject) = guest[name]?.let { name ->
        val lastSpace = name.lastIndexOf(' ')
        if (lastSpace >= 0) name.substring(0, lastSpace).trim().nullIfEmpty() else name
    }

    private fun lastNameFromName(guest: ModelObject) = guest[name]?.let { name ->
        val lastSpace = name.lastIndexOf(' ')
        if (lastSpace >= 0) name.substring(lastSpace + 1).trim().nullIfEmpty() else null
    }

    private fun birthDateFromAge(guest: ModelObject) = guest[age]?.let { age ->
        Date(year = Date().getFullYear() - age, month = 0)
    }

    private fun String?.nullIfEmpty() = if (this == "") null else this

    companion object Type : BaseModelTransformationType(PersonsMetamodel, GuestlistMetamodel) {
        override fun createChecked(leftModel: ChangeRecordingModel, rightModel: ChangeRecordingModel) =
            Persons2GuestsTransformation(leftModel, rightModel)
    }
}