import de.joshuagleitze.transformationnetwork.changerecording.BaseModelTransformation
import de.joshuagleitze.transformationnetwork.changerecording.ChangeRecordingModel
import de.joshuagleitze.transformationnetwork.changerecording.ObservableModelTransformationType
import de.joshuagleitze.transformationnetwork.changerecording.createChecked
import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.metametamodel.ofType
import de.joshuagleitze.transformationnetwork.models.primitives.PrimitivesMetamodel
import de.joshuagleitze.transformationnetwork.models.primitives.Word

class CharIncrement private constructor(
    override val leftModel: ChangeRecordingModel,
    override val rightModel: ChangeRecordingModel,
    override val type: Type
) : BaseModelTransformation() {
    private var leftWord
        get() = leftModel.valueContainer.value ?: ""
        set(value) {
            leftModel.valueContainer.value = value
        }
    private var rightWord
        get() = rightModel.valueContainer.value ?: ""
        set(value) {
            rightModel.valueContainer.value = value
        }

    override fun isConsistent() =
        leftWord == rightWord
            && leftWord.contains(leftChar) implies { leftWord.contains(leftChar + 1) }
            && rightWord.contains(rightChar) implies { rightWord.contains(rightChar + 1) }

    override fun processChangesChecked(leftSide: TransformationSide, rightSide: TransformationSide) {
        if (leftSide.changes.modifications.targetting(Word.Metaclass).isNotEmpty()) {
            processLeftChange()
        }
        if (rightSide.changes.modifications.targetting(Word.Metaclass).isNotEmpty()) {
            processRightChange()
        }
    }

    private fun processLeftChange() {
        val newWord = newWord(leftWord)
        leftWord = newWord
        rightWord = newWord
    }

    private fun processRightChange() {
        val newWord = newWord(rightWord)
        leftWord = newWord
        rightWord = newWord
    }

    private fun newWord(existingWord: String): String {
        var newWord = existingWord
        if (
            (existingWord.contains(leftChar) || (existingWord.contains(rightChar) && leftChar == rightChar + 1))
            && !existingWord.contains(leftChar + 1)
        ) {
            newWord += leftChar + 1
        }
        if (newWord.contains(rightChar) && !existingWord.contains(rightChar + 1)) {
            newWord += rightChar + 1
        }
        return newWord
    }

    private val Model.valueContainer get() = this.objects.ofType(Word.Metaclass).first()
    private val leftChar get() = type.leftChar
    private val rightChar get() = type.rightChar

    class Type(val leftChar: Char, val rightChar: Char) : ObservableModelTransformationType {
        override val leftMetamodel get() = PrimitivesMetamodel
        override val rightMetamodel get() = PrimitivesMetamodel

        override fun create(leftModel: Model, rightModel: Model) = createChecked(leftModel, rightModel) { left, right ->
            CharIncrement(left, right, this)
        }
    }

    companion object {
        fun changing(leftChar: Char, rightChar: Char) = Type(leftChar, rightChar)
    }
}
