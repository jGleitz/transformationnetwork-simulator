import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.ChangeSet
import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObject
import de.joshuagleitze.transformationnetwork.models.guestlist.Guest
import de.joshuagleitze.transformationnetwork.models.guestlist.GuestMetaclass.Attributes.age
import de.joshuagleitze.transformationnetwork.models.guestlist.GuestMetaclass.Attributes.name
import de.joshuagleitze.transformationnetwork.models.guestlist.GuestlistMetamodel
import de.joshuagleitze.transformationnetwork.models.persons.Person
import de.joshuagleitze.transformationnetwork.models.persons.PersonMetaclass.Attributes.birthDate
import de.joshuagleitze.transformationnetwork.models.persons.PersonMetaclass.Attributes.firstName
import de.joshuagleitze.transformationnetwork.models.persons.PersonMetaclass.Attributes.lastName
import de.joshuagleitze.transformationnetwork.models.persons.PersonsMetamodel
import de.joshuagleitze.transformationnetwork.modeltransformation.DefaultModelTransformation
import de.joshuagleitze.transformationnetwork.modeltransformation.DefaultModelTransformationType
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation.Side.LEFT
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation.Side.RIGHT
import kotlin.js.Date

private const val MILLIS_IN_YEAR = 1_000L * 60L * 60L * 24L * 365L

class Persons2GuestsTransformation(val personsModel: Model, val guestlistModel: Model) :
    DefaultModelTransformation<Nothing>() {
    override val leftModel: Model get() = personsModel
    override val rightModel: Model get() = guestlistModel
    override val type get() = Companion

    override fun processChangesChecked(leftSide: TransformationSide, rightSide: TransformationSide) {
        leftSide.processDeletions()
        rightSide.processDeletions()
        leftSide.processAdditions()
        rightSide.processAdditions()
        processPersonModifications(leftSide)
        processGuestModifications(rightSide)
    }

    private fun TransformationSide.processDeletions() {
        deletions.forEach { deletion ->
            getOtherSideCorrespondence(deletion.deletedObject)?.let { otherSideObject ->
                otherSideModel -= otherSideObject
                correspondences.removeCorrespondence(deletion.deletedObject)
            }
        }
    }

    private fun TransformationSide.processAdditions() {
        additions.forEach { addition ->
            val otherSidePersonLike = createOtherSidePersonLike(addition.addedObject)
            otherSideModel += otherSidePersonLike
            correspondences.addCorrespondence(side, addition.addedObject, otherSidePersonLike)
        }
    }

    private fun TransformationSide.createOtherSidePersonLike(personLike: ModelObject): ModelObject = when (side) {
        LEFT -> Guest().apply {
            name = nameFromFirstNameAndLastName(personLike)
            age = ageFromBirthDate(personLike)
        }
        RIGHT -> Person().apply {
            firstName = firstNameFromName(personLike)
            lastName = lastNameFromName(personLike)
            birthDate = birthDateFromAge(personLike)
        }
    }

    private fun processPersonModifications(personChanges: ChangeSet) {
        personChanges.modifications.forEach { modification ->
            val person = modification.targetObject
            val correspondingGuest = correspondences.getRightCorrespondence(person)
            checkNotNull(correspondingGuest) { "Cannot find the corresponding guest for '$person'!" }
            when (modification.targetAttribute) {
                firstName, lastName -> correspondingGuest[name] = nameFromFirstNameAndLastName(person)
                birthDate -> correspondingGuest[age] = ageFromBirthDate(person)
            }
        }
    }

    private fun processGuestModifications(guestChanges: ChangeSet) {
        guestChanges.modifications.forEach { modification ->
            val guest = modification.targetObject
            val correspondingPerson = correspondences.getLeftCorrespondence(guest)
            checkNotNull(correspondingPerson) { "Cannot find the corresponding person for '$guest'!" }
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

    companion object : DefaultModelTransformationType(PersonsMetamodel, GuestlistMetamodel) {
        override fun createChecked(leftModel: Model, rightModel: Model) =
            Persons2GuestsTransformation(leftModel, rightModel)
    }
}