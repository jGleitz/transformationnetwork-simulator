import de.joshuagleitze.transformationnetwork.changerecording.BaseModelTransformation
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecordingModel
import de.joshuagleitze.transformationnetwork.changerecording.ObservableModelTransformationType
import de.joshuagleitze.transformationnetwork.changerecording.createChecked
import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.metametamodel.ofType
import de.joshuagleitze.transformationnetwork.models.primitives.Number
import de.joshuagleitze.transformationnetwork.models.primitives.PrimitivesMetamodel

class Incrementing private constructor(
    override val leftModel: ChangeRecordingModel,
    override val rightModel: ChangeRecordingModel,
    override val type: Type
) : BaseModelTransformation() {
    private var leftNumber
        get() = leftModel.valueContainer.value ?: 0
        set(value) {
            leftModel.valueContainer.value = value
        }
    private var rightNumber
        get() = rightModel.valueContainer.value ?: 0
        set(value) {
            rightModel.valueContainer.value = value
        }

    override fun isConsistent() =
        (leftNumber in minimum..maximum) implies {
            leftNumber < rightNumber
        }

    override fun processChangesChecked(leftSide: TransformationSide, rightSide: TransformationSide) {
        if (leftSide.changes.modifications.targetting(Number.Metaclass).isNotEmpty()) {
            processLeftChange()
        }
    }

    private fun processLeftChange() {
        if (leftNumber in minimum..maximum && leftNumber >= rightNumber) {
            rightNumber = leftNumber + 1
        }
    }

    private val Model.valueContainer get() = this.objects.ofType(Number.Metaclass).first()
    private val minimum get() = type.minimum
    private val maximum get() = type.maximum

    class Type(val minimum: Int, val maximum: Int) : ObservableModelTransformationType {
        override val leftMetamodel get() = PrimitivesMetamodel
        override val rightMetamodel get() = PrimitivesMetamodel

        override fun create(leftModel: Model, rightModel: Model) = createChecked(leftModel, rightModel) { left, right ->
            Incrementing(left, right, this)
        }
    }

    companion object {
        fun between(minimum: Int, maximum: Int) = Type(minimum, maximum)
    }
}
