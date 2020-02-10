package de.joshuagleitze.transformationnetwork.changerecording

import de.joshuagleitze.transformationnetwork.changemetamodel.AttributeChange
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.ChangeSet
import de.joshuagleitze.transformationnetwork.metametamodel.MetaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.Metaclass
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObject
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObjectIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.byIdentity
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelCorrespondences
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation.Side.LEFT
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation.Side.RIGHT
import de.joshuagleitze.transformationnetwork.publishsubscribe.Observable
import de.joshuagleitze.transformationnetwork.publishsubscribe.PublishingObservable

abstract class BaseModelTransformation<in Tag : Any> : ObservableModelTransformation {
    private val _execution = PublishingObservable<Unit>()
    override val execution: Observable<Unit> get() = _execution
    protected val correspondences: ModelCorrespondences<Tag> = Correspondences()
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
        override fun get(side: ModelTransformation.Side) = when (side) {
            LEFT -> leftModel
            RIGHT -> rightModel
        }
    }

    protected inline fun Iterable<AttributeChange>.executeFiltered(
        metaclass: Metaclass,
        function: (AttributeChange) -> Unit
    ) {
        this.filter { it.targetObject.metaclass == metaclass }.forEach(function)
    }

    protected fun TransformationSide.propagateDeletionsToOtherSide() {
        deletions.forEach { deletion ->
            val deletedObject = thisSideModel.requireObject(deletion.deletedObject)
            getOtherSideCorrespondence(deletedObject)?.let { otherSideObject ->
                otherSideModel -= otherSideObject
                correspondences.removeCorrespondence(deletedObject)
            }
        }
    }

    protected fun getFromModel(
        side: ModelTransformation.Side,
        modelObjectIdentity: ModelObjectIdentity
    ): ModelObject =
        checkNotNull(models[side].objects.find { modelObjectIdentity.identifies(it) }) { "The $side model '${models[side]}' does not contain the object '$modelObjectIdentity'!" }

    override fun toString() = "${this::class.simpleName}[$leftModel <-> $rightModel]"

    private inner class Correspondences :
        ModelCorrespondences<Tag> {
        private val leftToRight: MutableMap<Pair<ModelObjectIdentity, Tag?>, ModelObjectIdentity> = HashMap()
        private val rightToLeft: MutableMap<Pair<ModelObjectIdentity, Tag?>, ModelObjectIdentity> = HashMap()

        override fun addCorrespondence(
            firstObjectSide: ModelTransformation.Side,
            firstObject: ModelObjectIdentity,
            secondObject: ModelObjectIdentity,
            tag: Tag?
        ) {
            // TODO
            //getFromModel(firstObjectSide, firstObject)
            //getFromModel(firstObjectSide.opposite, secondObject)
            getToOtherSideMap(firstObjectSide)[firstObject to tag] = secondObject
            getToOtherSideMap(firstObjectSide.opposite)[secondObject to tag] = firstObject
        }

        override fun removeCorrespondence(rightOrLeftModelObject: ModelObjectIdentity, tag: Tag?) {
            val modelSide = findModelSide(rightOrLeftModelObject)
            val currentlyAssociated = getToOtherSideMap(modelSide).remove(rightOrLeftModelObject to tag)
            checkNotNull(currentlyAssociated) { "$rightOrLeftModelObject is not currently associated with any object!" }
            getToOtherSideMap(modelSide.opposite) -= currentlyAssociated to tag
        }

        private fun findModelSide(modelObject: ModelObjectIdentity) = when {
            leftModel.containsObject(byIdentity(modelObject)) -> LEFT
            rightModel.containsObject(byIdentity(modelObject)) -> RIGHT
            else -> throw AssertionError("'$modelObject' is neither in the left nor right model!")
        }

        override fun getCorrespondence(
            modelObject: ModelObjectIdentity,
            objectSide: ModelTransformation.Side,
            tag: Tag?
        ): ModelObjectIdentity? {
            // TODO
            //getFromModel(objectSide, modelObject)
            return getToOtherSideMap(objectSide)[modelObject to tag]
        }

        override fun getCorrespondence(
            modelObject: ModelObject,
            objectSide: ModelTransformation.Side,
            tag: Tag?
        ): ModelObject? {
            val correspondenceIdentity = getCorrespondence(modelObject.identity, objectSide, tag)
            return if (correspondenceIdentity != null) getFromModel(objectSide.opposite, correspondenceIdentity)
            else null
        }

        private fun getToOtherSideMap(side: ModelTransformation.Side) = when (side) {
            LEFT -> leftToRight
            RIGHT -> rightToLeft
        }
    }

    protected inner class TransformationSide(val side: ModelTransformation.Side, val changes: ChangeSet) :
        ChangeSet by changes {
        val thisSideModel get() = models[side]
        val otherSideModel get() = models[side.opposite]

        fun getOtherSideCorrespondence(modelObject: ModelObject): ModelObject? {
            val correspondenceIdentity = correspondences.getCorrespondence(modelObject.identity, side)
            return if (correspondenceIdentity != null) otherSideModel.requireObject(byIdentity(correspondenceIdentity)) else null
        }
    }

    protected inline fun <T : Any> ModelObject.changeIfNot(
        metaAttribute: MetaAttribute<T>,
        vararg assignments: AttributeAssignment<*>,
        valueProvider: () -> T?
    ) {
        if (assignments.any { !it.holdsWith(this) }) {
            this[metaAttribute] = valueProvider()
        }
    }

    protected class AttributeAssignment<in Target>(
        private val value: Target,
        private val transformation: (ModelObject) -> Target
    ) {
        fun holdsWith(sourceObject: ModelObject) = value == transformation(sourceObject)
    }

    protected infix fun <Target> Target.isEqualTo(valueSource: (ModelObject) -> Target) =
        AttributeAssignment(
            this,
            valueSource
        )
}
