package de.joshuagleitze.transformationnetwork.changerecording

import de.joshuagleitze.transformationnetwork.changemetamodel.AdditionChange
import de.joshuagleitze.transformationnetwork.changemetamodel.AttributeChange
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.ChangeSet
import de.joshuagleitze.transformationnetwork.metametamodel.AnyModelObject
import de.joshuagleitze.transformationnetwork.metametamodel.AnyModelObjectIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.MetaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.Metaclass
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObject
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObjectIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.byIdentity
import de.joshuagleitze.transformationnetwork.modeltransformation.AnyCorrespondenceTag
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelCorrespondences
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelCorrespondences.CorrespondenceTag
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation.Side
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation.Side.LEFT
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation.Side.RIGHT
import de.joshuagleitze.transformationnetwork.publishsubscribe.Observable
import de.joshuagleitze.transformationnetwork.publishsubscribe.PublishingObservable

abstract class BaseModelTransformation : ObservableModelTransformation {
    private val _execution = PublishingObservable<Unit>()
    override val execution: Observable<Unit> get() = _execution
    protected val correspondences: ModelCorrespondences = Correspondences()
    override val models: ObservableModelTransformation.TransformationModels = DefaultTransformationModels()

    protected abstract fun processChangesChecked(leftSide: TransformationSide, rightSide: TransformationSide)

    final override fun processChanges(leftChanges: ChangeSet, rightChanges: ChangeSet) {
        leftChanges.forEach { change ->
            check(change.targetModel.identifies(leftModel)) { "The left change '$change' does not target this transformation’s left model '$leftModel'!" }
        }
        rightChanges.forEach { change ->
            check(change.targetModel.identifies(rightModel)) { "The right change '$change' does not target this transformation’s right model '$rightModel'!" }
        }
        _execution.publish(Unit)
        processChangesChecked(TransformationSide(LEFT, leftChanges), TransformationSide(RIGHT, rightChanges))
    }

    private inner class DefaultTransformationModels : ObservableModelTransformation.TransformationModels {
        override fun get(side: Side) = when (side) {
            LEFT -> leftModel
            RIGHT -> rightModel
        }
    }

    protected fun TransformationSide.propagateDeletionsToOtherSide(vararg tags: AnyCorrespondenceTag) =
        propagateDeletionsToSide(side.opposite, *tags)

    protected fun TransformationSide.propagateDeletionsOnThisSide(vararg tags: AnyCorrespondenceTag) =
        propagateDeletionsToSide(side, *tags)

    private fun TransformationSide.propagateDeletionsToSide(side: Side, vararg tags: AnyCorrespondenceTag) {
        tags.forEach { tag ->
            deletions.forEach { deletion ->
                val deletedObject = thisSideModel.requireObject(deletion.deletedObject)
                correspondences.getCorrespondence(deletedObject, side, tag)?.let { otherSideObject ->
                    otherSideModel -= otherSideObject
                    correspondences.removeCorrespondence(deletedObject, tag)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <O : ModelObject<O>> Iterable<AttributeChange<*>>.targetting(metaclass: Metaclass<O>) =
        this.mapNotNull { it.takeIf { it.targetObject.metaclass == metaclass } as AttributeChange<O>? }

    @Suppress("UNCHECKED_CAST")
    fun <O : ModelObject<O>> Iterable<AdditionChange<*>>.adding(metaclass: Metaclass<O>) =
        this.mapNotNull { it.takeIf { it.addedObjectClass == metaclass } as AdditionChange<O>? }

    @Suppress("UNCHECKED_CAST")
    protected fun <O : ModelObject<O>> getFromModel(
        side: Side,
        modelObjectIdentity: ModelObjectIdentity<O>
    ): O = checkNotNull(models[side].objects.find { modelObjectIdentity.identifies(it) }) {
        "The $side model '${models[side]}' does not contain the object '$modelObjectIdentity'!"
    } as O

    override fun toString() = "${this::class.simpleName}[$leftModel <-> $rightModel]"

    private inner class Correspondences : ModelCorrespondences {
        private val leftToLeft: MutableMap<Pair<AnyModelObjectIdentity, AnyCorrespondenceTag>, AnyModelObjectIdentity> =
            HashMap()
        private val rightToRight: MutableMap<Pair<AnyModelObjectIdentity, AnyCorrespondenceTag>, AnyModelObjectIdentity> =
            HashMap()

        private val leftToRight: MutableMap<Pair<AnyModelObjectIdentity, AnyCorrespondenceTag>, AnyModelObjectIdentity> =
            HashMap()
        private val rightToLeft: MutableMap<Pair<AnyModelObjectIdentity, AnyCorrespondenceTag>, AnyModelObjectIdentity> =
            HashMap()

        private fun addOppositeCorrespondence(
            firstObjectSide: Side,
            firstObject: AnyModelObjectIdentity,
            secondObject: AnyModelObjectIdentity,
            tag: AnyCorrespondenceTag
        ) {
            getFromModel(firstObjectSide, firstObject)
            getFromModel(firstObjectSide.opposite, secondObject)
            getFromOtherSideMap(firstObjectSide.opposite)[firstObject to tag] = secondObject
            getFromOtherSideMap(firstObjectSide)[secondObject to tag] = firstObject
        }

        private fun addSameSideCorrespondence(
            side: Side,
            firstObject: AnyModelObjectIdentity,
            secondObject: AnyModelObjectIdentity,
            tag: AnyCorrespondenceTag
        ) {
            getFromModel(side, firstObject)
            getFromModel(side, secondObject)
            getSameSideMap(side)[firstObject to tag] = secondObject
            getSameSideMap(side)[secondObject to tag] = firstObject
        }

        override fun removeCorrespondence(rightOrLeftModelObject: AnyModelObjectIdentity, tag: AnyCorrespondenceTag) {
            val modelSide = findModelSide(rightOrLeftModelObject)
            val currentlyAssociated = getFromOtherSideMap(modelSide.opposite).remove(rightOrLeftModelObject to tag)
            checkNotNull(currentlyAssociated) { "$rightOrLeftModelObject is not currently associated with any object!" }
            getFromOtherSideMap(modelSide) -= currentlyAssociated to tag
        }

        private fun findModelSide(identity: AnyModelObjectIdentity) = when {
            leftModel.containsObject(byIdentity(identity)) -> LEFT
            rightModel.containsObject(byIdentity(identity)) -> RIGHT
            else -> throw AssertionError("'$identity' is neither in the left nor right model!")
        }

        override fun getCorrespondence(
            identity: AnyModelObjectIdentity,
            targetSide: Side,
            tag: AnyCorrespondenceTag
        ): AnyModelObject? {
            val correspondenceIdentity =
                getFromOtherSideMap(targetSide)[identity to tag] ?: getSameSideMap(targetSide)[identity to tag]
            return if (correspondenceIdentity != null) getFromModel(targetSide, correspondenceIdentity)
            else null
        }

        private fun requireCorrespondence(
            modelObject: AnyModelObjectIdentity,
            targetSide: Side,
            tag: AnyCorrespondenceTag
        ) = checkNotNull(getCorrespondence(modelObject, targetSide, tag)) {
            "Cannot find a correspondence for $modelObject in the ${targetSide.name} model!"
        }

        override fun <Left : ModelObject<Left>, Right : ModelObject<Right>> addLeftToRightCorrespondence(
            leftModelObject: ModelObjectIdentity<Left>,
            rightModelObject: ModelObjectIdentity<Right>,
            tag: CorrespondenceTag<Left, Right>
        ) = addOppositeCorrespondence(LEFT, leftModelObject, rightModelObject, tag)

        override fun <First : ModelObject<First>, Second : ModelObject<Second>> addLeftToLeftCorrespondence(
            firstModelObject: ModelObjectIdentity<First>,
            secondModelObject: ModelObjectIdentity<Second>,
            tag: CorrespondenceTag<First, Second>
        ) = addSameSideCorrespondence(LEFT, firstModelObject, secondModelObject, tag)

        override fun <First : ModelObject<First>, Second : ModelObject<Second>> addRightToRightCorrespondence(
            firstModelObject: ModelObjectIdentity<First>,
            secondModelObject: ModelObjectIdentity<Second>,
            tag: CorrespondenceTag<First, Second>
        ) = addSameSideCorrespondence(RIGHT, firstModelObject, secondModelObject, tag)

        @Suppress("UNCHECKED_CAST")
        override fun <Left : ModelObject<Left>, Right : ModelObject<Right>> getRightCorrespondence(
            leftModelObject: ModelObjectIdentity<Left>,
            tag: CorrespondenceTag<Left, Right>
        ) = getCorrespondence(leftModelObject, RIGHT, tag) as Right?

        @Suppress("UNCHECKED_CAST")
        override fun <Left : ModelObject<Left>, Right : ModelObject<Right>> requireRightCorrespondence(
            leftModelObject: ModelObjectIdentity<Left>,
            tag: CorrespondenceTag<Left, Right>
        ) = requireCorrespondence(leftModelObject, RIGHT, tag) as Right

        @Suppress("UNCHECKED_CAST")
        override fun <Left : ModelObject<Left>, Right : ModelObject<Right>> getLeftCorrespondence(
            rightModelObject: ModelObjectIdentity<Right>,
            tag: CorrespondenceTag<Left, Right>
        ) = getCorrespondence(rightModelObject, LEFT, tag) as Left?

        @Suppress("UNCHECKED_CAST")
        override fun <Left : ModelObject<Left>, Right : ModelObject<Right>> requireLeftCorrespondence(
            rightModelObject: ModelObjectIdentity<Right>,
            tag: CorrespondenceTag<Left, Right>
        ) = requireCorrespondence(rightModelObject, LEFT, tag) as Left

        @Suppress("UNCHECKED_CAST")
        override fun <First : ModelObject<First>, Second : ModelObject<Second>> getLeftSecondCorrespondence(
            firstModelObject: ModelObjectIdentity<First>,
            tag: CorrespondenceTag<First, Second>
        ) = getCorrespondence(firstModelObject, LEFT, tag) as Second?

        @Suppress("UNCHECKED_CAST")
        override fun <First : ModelObject<First>, Second : ModelObject<Second>> requireLeftSecondCorrespondence(
            firstModelObject: ModelObjectIdentity<First>,
            tag: CorrespondenceTag<First, Second>
        ) = requireCorrespondence(firstModelObject, LEFT, tag) as Second

        private fun getFromOtherSideMap(side: Side) = when (side) {
            LEFT -> rightToLeft
            RIGHT -> leftToRight
        }

        private fun getSameSideMap(side: Side) = when (side) {
            LEFT -> leftToLeft
            RIGHT -> rightToRight
        }
    }

    protected inner class TransformationSide(val side: Side, val changes: ChangeSet) :
        ChangeSet by changes {
        val thisSideModel get() = models[side]
        val otherSideModel get() = models[side.opposite]
    }

    protected inline fun <O : ModelObject<O>, T : Any> O.changeIfNot(
        metaAttribute: MetaAttribute<T>,
        vararg assignments: AttributeAssignment<O, *>,
        valueProvider: () -> T?
    ) {
        if (assignments.any { !it.holdsWith(this) }) {
            this[metaAttribute] = valueProvider()
        }
    }

    protected class AttributeAssignment<O : ModelObject<*>, in Target>(
        private val value: Target,
        private val transformation: (O) -> Target
    ) {
        fun holdsWith(sourceObject: O) = value == transformation(sourceObject)
    }

    protected infix fun <O : ModelObject<O>, Target> Target.isEqualTo(valueSource: (O) -> Target) =
        AttributeAssignment(this, valueSource)
}
