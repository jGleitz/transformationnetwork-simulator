package de.joshuagleitze.transformationnetwork.modeltransformation

import de.joshuagleitze.transformationnetwork.metametamodel.ModelObject
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation.Side.LEFT
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation.Side.RIGHT

interface ModelCorrespondences<in Tag : Any> {
    fun addLeftToRightCorrespondence(leftModelObject: ModelObject, rightModelObject: ModelObject, tag: Tag? = null) =
        addCorrespondence(LEFT, leftModelObject, rightModelObject, tag)

    fun addCorrespondence(
        firstObjectSide: ModelTransformation.Side,
        firstObject: ModelObject,
        secondObject: ModelObject,
        tag: Tag? = null
    )

    fun removeCorrespondence(rightOrLeftModelObject: ModelObject, tag: Tag? = null)

    fun getCorrespondence(
        modelObject: ModelObject,
        objectSide: ModelTransformation.Side,
        tag: Tag? = null
    ): ModelObject?

    fun getRightCorrespondence(leftModelObject: ModelObject, tag: Tag? = null) =
        getCorrespondence(leftModelObject, LEFT, tag)

    fun getLeftCorrespondence(rightModelObject: ModelObject, tag: Tag? = null) =
        getCorrespondence(rightModelObject, RIGHT, tag)
}