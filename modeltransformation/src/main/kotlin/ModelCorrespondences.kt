package de.joshuagleitze.transformationnetwork.modeltransformation

import de.joshuagleitze.transformationnetwork.metametamodel.ModelObject
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObjectIdentity
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation.Side.LEFT
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation.Side.RIGHT

interface ModelCorrespondences<in Tag : Any> {
    fun addLeftToRightCorrespondence(
        leftModelObject: ModelObjectIdentity,
        rightModelObject: ModelObjectIdentity,
        tag: Tag? = null
    ) = addCorrespondence(LEFT, leftModelObject, rightModelObject, tag)

    fun addLeftToRightCorrespondence(
        leftModelObject: ModelObject,
        rightModelObject: ModelObject,
        tag: Tag? = null
    ) = addCorrespondence(LEFT, leftModelObject, rightModelObject, tag)

    fun addCorrespondence(
        firstObjectSide: ModelTransformation.Side,
        firstObject: ModelObjectIdentity,
        secondObject: ModelObjectIdentity,
        tag: Tag? = null
    )

    fun addCorrespondence(
        firstObjectSide: ModelTransformation.Side,
        firstObject: ModelObject,
        secondObject: ModelObject,
        tag: Tag? = null
    ) = addCorrespondence(firstObjectSide, firstObject.identity, secondObject.identity, tag)

    fun removeCorrespondence(rightOrLeftModelObject: ModelObjectIdentity, tag: Tag? = null)
    fun removeCorrespondence(rightOrLeftModelObject: ModelObject, tag: Tag? = null) =
        removeCorrespondence(rightOrLeftModelObject.identity, tag)

    fun getCorrespondence(
        modelObject: ModelObjectIdentity,
        objectSide: ModelTransformation.Side,
        tag: Tag? = null
    ): ModelObjectIdentity?

    fun requireCorrespondence(
        modelObject: ModelObjectIdentity,
        objectSide: ModelTransformation.Side,
        tag: Tag? = null
    ) = checkNotNull(getCorrespondence(modelObject, objectSide, tag))
    { "Cannot find a correspondence for $modelObject in the ${objectSide.name} model!" }

    fun getCorrespondence(
        modelObject: ModelObject,
        objectSide: ModelTransformation.Side,
        tag: Tag? = null
    ): ModelObject?

    fun requireCorrespondence(
        modelObject: ModelObject,
        objectSide: ModelTransformation.Side,
        tag: Tag? = null
    ) = checkNotNull(getCorrespondence(modelObject, objectSide, tag))
    { "Cannot find a correspondence for $modelObject in the ${objectSide.name} model!" }

    fun getRightCorrespondence(leftModelObject: ModelObjectIdentity, tag: Tag? = null) =
        getCorrespondence(leftModelObject, LEFT, tag)

    fun requireRightCorrespondence(leftModelObject: ModelObjectIdentity, tag: Tag? = null) =
        requireCorrespondence(leftModelObject, LEFT, tag)

    fun getRightCorrespondence(leftModelObject: ModelObject, tag: Tag? = null) =
        getCorrespondence(leftModelObject, LEFT, tag)

    fun requireRightCorrespondence(leftModelObject: ModelObject, tag: Tag? = null) =
        requireCorrespondence(leftModelObject, LEFT, tag)

    fun getLeftCorrespondence(rightModelObject: ModelObjectIdentity, tag: Tag? = null) =
        getCorrespondence(rightModelObject, RIGHT, tag)

    fun requireLeftCorrespondence(rightModelObject: ModelObjectIdentity, tag: Tag? = null) =
        requireCorrespondence(rightModelObject, RIGHT, tag)

    fun getLeftCorrespondence(rightModelObject: ModelObject, tag: Tag? = null) =
        getCorrespondence(rightModelObject, RIGHT, tag)

    fun requireLeftCorrespondence(rightModelObject: ModelObject, tag: Tag? = null) =
        requireCorrespondence(rightModelObject, RIGHT, tag)
}