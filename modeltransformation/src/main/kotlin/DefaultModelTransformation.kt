package de.joshuagleitze.transformationnetwork.modeltransformation

import de.joshuagleitze.transformationnetwork.changemetamodel.ChangeSet
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObject
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation.Side.LEFT
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation.Side.RIGHT

abstract class DefaultModelTransformation<in Tag : Any> : ModelTransformation {
    protected val correspondences: ModelCorrespondences<Tag> = Correspondences()
    override val models: ModelTransformation.TransformationModels = DefaultTransformationModels()

    protected abstract fun processChangesChecked(leftSide: TransformationSide, rightSide: TransformationSide)

    final override fun processChanges(leftChanges: ChangeSet, rightChanges: ChangeSet) {
        leftChanges.forEach { change ->
            check(change.targetModel == leftModel) { "The left change '$change' does not target this transformation’s left model '$leftModel'!" }
        }
        rightChanges.forEach { change ->
            check(change.targetModel == leftModel) { "The right change '$change' does not target this transformation’s right model '$rightModel'!" }
        }
        processChangesChecked(TransformationSide(LEFT, leftChanges), TransformationSide(RIGHT, rightChanges))
    }

    private inner class DefaultTransformationModels : ModelTransformation.TransformationModels {
        override fun get(side: ModelTransformation.Side) = when (side) {
            LEFT -> leftModel
            RIGHT -> rightModel
        }
    }

    protected fun checkIsFromModel(side: ModelTransformation.Side, modelObject: ModelObject) =
        check(models[side].objects.contains(modelObject)) { "The $side model '${models[side]}' does not contain the object '$modelObject'!" }

    private inner class Correspondences : ModelCorrespondences<Tag> {
        private val leftToRight: MutableMap<Pair<ModelObject, Tag?>, ModelObject> = HashMap()

        private val rightToLeft: MutableMap<Pair<ModelObject, Tag?>, ModelObject> = HashMap()

        override fun addCorrespondence(
            firstObjectSide: ModelTransformation.Side,
            firstObject: ModelObject,
            secondObject: ModelObject,
            tag: Tag?
        ) {
            checkIsFromModel(firstObjectSide, firstObject)
            checkIsFromModel(firstObjectSide.opposite, secondObject)
            getToOtherSideMap(firstObjectSide)[firstObject to tag] = secondObject
            getToOtherSideMap(firstObjectSide.opposite)[secondObject to tag] = firstObject
        }

        override fun removeCorrespondence(rightOrLeftModelObject: ModelObject, tag: Tag?) {
            val modelSide = findModelSide(rightOrLeftModelObject)
            val currentlyAssociated = getToOtherSideMap(modelSide).remove(rightOrLeftModelObject to tag)
            checkNotNull(currentlyAssociated) { "$rightOrLeftModelObject is not currently associated with any object!" }
            getToOtherSideMap(modelSide.opposite) -= currentlyAssociated to tag
        }

        private fun findModelSide(modelObject: ModelObject) = when {
            leftModel.objects.contains(modelObject) -> LEFT
            rightModel.objects.contains(modelObject) -> RIGHT
            else -> throw AssertionError("'$modelObject' is neither in the left nor right model!")
        }

        override fun getCorrespondence(
            modelObject: ModelObject,
            objectSide: ModelTransformation.Side,
            tag: Tag?
        ): ModelObject? {
            checkIsFromModel(objectSide, modelObject)
            return getToOtherSideMap(objectSide)[modelObject to tag]
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

        fun getOtherSideCorrespondence(modelObject: ModelObject) = correspondences.getCorrespondence(modelObject, side)
    }
}