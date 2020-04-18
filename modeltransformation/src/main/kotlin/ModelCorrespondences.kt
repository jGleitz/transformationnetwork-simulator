package de.joshuagleitze.transformationnetwork.modeltransformation

import de.joshuagleitze.transformationnetwork.metametamodel.AnyModelObject
import de.joshuagleitze.transformationnetwork.metametamodel.AnyModelObjectIdentity
import de.joshuagleitze.transformationnetwork.metametamodel.Metaclass
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObject
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObjectIdentity

interface ModelCorrespondences {
    fun <Left : ModelObject<Left>, Right : ModelObject<Right>> addLeftToRightCorrespondence(
        leftModelObject: ModelObjectIdentity<Left>,
        rightModelObject: ModelObjectIdentity<Right>,
        tag: CorrespondenceTag<Left, Right>
    )

    fun <Left : ModelObject<Left>, Right : ModelObject<Right>> addLeftToRightCorrespondence(
        leftModelObject: Left,
        rightModelObject: Right,
        tag: CorrespondenceTag<Left, Right>
    ) = addLeftToRightCorrespondence(leftModelObject.identity, rightModelObject.identity, tag)

    fun <First : ModelObject<First>, Second : ModelObject<Second>> addLeftToLeftCorrespondence(
        firstModelObject: ModelObjectIdentity<First>,
        secondModelObject: ModelObjectIdentity<Second>,
        tag: CorrespondenceTag<First, Second>
    )

    fun <First : ModelObject<First>, Second : ModelObject<Second>> addLeftToLeftCorrespondence(
        firstModelObject: First,
        secondModelObject: Second,
        tag: CorrespondenceTag<First, Second>
    ) = addLeftToLeftCorrespondence(firstModelObject.identity, secondModelObject.identity, tag)


    fun <First : ModelObject<First>, Second : ModelObject<Second>> addRightToRightCorrespondence(
        firstModelObject: ModelObjectIdentity<First>,
        secondModelObject: ModelObjectIdentity<Second>,
        tag: CorrespondenceTag<First, Second>
    )

    fun <First : ModelObject<First>, Second : ModelObject<Second>> addRightToRightCorrespondence(
        firstModelObject: First,
        secondModelObject: Second,
        tag: CorrespondenceTag<First, Second>
    ) = addRightToRightCorrespondence(firstModelObject.identity, secondModelObject.identity, tag)

    fun removeCorrespondence(rightOrLeftModelObject: ModelObjectIdentity<*>, tag: CorrespondenceTag<*, *>)
    fun removeCorrespondence(rightOrLeftModelObject: ModelObject<*>, tag: CorrespondenceTag<*, *>) =
        removeCorrespondence(rightOrLeftModelObject.identity, tag)

    fun getCorrespondence(
        identity: AnyModelObjectIdentity,
        targetSide: ModelTransformation.Side,
        tag: AnyCorrespondenceTag
    ): AnyModelObject?

    fun getCorrespondence(
        modelObject: AnyModelObject,
        targetSide: ModelTransformation.Side,
        tag: AnyCorrespondenceTag
    ) = getCorrespondence(modelObject.identity, targetSide, tag)

    fun <Left : ModelObject<Left>, Right : ModelObject<Right>> getRightCorrespondence(
        leftModelObject: ModelObjectIdentity<Left>,
        tag: CorrespondenceTag<Left, Right>
    ): Right?

    fun <Left : ModelObject<Left>, Right : ModelObject<Right>> requireRightCorrespondence(
        leftModelObject: ModelObjectIdentity<Left>,
        tag: CorrespondenceTag<Left, Right>
    ): Right

    fun <Left : ModelObject<Left>, Right : ModelObject<Right>> getRightCorrespondence(
        leftModelObject: Left,
        tag: CorrespondenceTag<Left, Right>
    ) = getRightCorrespondence(leftModelObject.identity, tag)

    fun <Left : ModelObject<Left>, Right : ModelObject<Right>> requireRightCorrespondence(
        firstModelObject: Left,
        tag: CorrespondenceTag<Left, Right>
    ) = requireRightCorrespondence(firstModelObject.identity, tag)

    fun <Left : ModelObject<Left>, Right : ModelObject<Right>> getLeftCorrespondence(
        rightModelObject: ModelObjectIdentity<Right>,
        tag: CorrespondenceTag<Left, Right>
    ): Left?

    fun <Left : ModelObject<Left>, Right : ModelObject<Right>> requireLeftCorrespondence(
        rightModelObject: ModelObjectIdentity<Right>,
        tag: CorrespondenceTag<Left, Right>
    ): Left

    fun <Left : ModelObject<Left>, Right : ModelObject<Right>> getLeftCorrespondence(
        rightModelObject: Right,
        tag: CorrespondenceTag<Left, Right>
    ) = getLeftCorrespondence(rightModelObject.identity, tag)

    fun <Left : ModelObject<Left>, Right : ModelObject<Right>> requireLeftCorrespondence(
        rightModelObject: Right,
        tag: CorrespondenceTag<Left, Right>
    ) = requireLeftCorrespondence(rightModelObject.identity, tag)

    fun <First : ModelObject<First>, Second : ModelObject<Second>> getLeftSecondCorrespondence(
        firstModelObject: ModelObjectIdentity<First>,
        tag: CorrespondenceTag<First, Second>
    ): Second?

    fun <First : ModelObject<First>, Second : ModelObject<Second>> requireLeftSecondCorrespondence(
        firstModelObject: ModelObjectIdentity<First>,
        tag: CorrespondenceTag<First, Second>
    ): Second

    fun <First : ModelObject<First>, Second : ModelObject<Second>> getLeftSecondCorrespondence(
        firstModelObject: First,
        tag: CorrespondenceTag<First, Second>
    ) = getLeftSecondCorrespondence(firstModelObject.identity, tag)

    fun <First : ModelObject<First>, Second : ModelObject<Second>> requireLeftSecondCorrespondence(
        firstModelObject: First,
        tag: CorrespondenceTag<First, Second>
    ) = requireLeftSecondCorrespondence(firstModelObject.identity, tag)


    data class CorrespondenceTag<First : ModelObject<First>, Second : ModelObject<Second>>(
        val leftClass: Metaclass<First>,
        val rightClass: Metaclass<Second>
    )
}
typealias AnyCorrespondenceTag = ModelCorrespondences.CorrespondenceTag<*, *>
