package de.joshuagleitze.transformationnetwork.simulator.scenarios

import CharIncrement
import de.joshuagleitze.transformationnetwork.changemetamodel.AttributeSetChange
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.changeSetOf
import de.joshuagleitze.transformationnetwork.changerecording.factory.model
import de.joshuagleitze.transformationnetwork.models.primitives.PrimitivesMetamodel
import de.joshuagleitze.transformationnetwork.models.primitives.Word.Metaclass.Attributes.value
import de.joshuagleitze.transformationnetwork.models.primitives.withWord
import de.joshuagleitze.transformationnetwork.simulator.data.scenario.SimulatorScenario
import de.joshuagleitze.transformationnetwork.simulator.data.scenario.at
import de.joshuagleitze.transformationnetwork.simulator.util.geometry.x

object A2B {
    fun create(transformationCount: Int): SimulatorScenario {
        val size = transformationCount + 1
        fun permute(i: Int) = when {
            i % 2 == 0 -> (size - 1) - i / 2
            else -> size / 2 - i / 2 - 1
        }

        val models = (0 until size).map { index ->
            PrimitivesMetamodel
                .model("${char(permute(index)).toUpperCase()} → ${(char(permute(index)) + 1).toUpperCase()}")
                .withWord("")
                .at((index + 1) x 1)
        }
        val transformations = (0 until size)
            .windowed(2)
            .map { (leftIndex, rightIndex) ->
                CharIncrement.changing(char(permute(leftIndex)), char(permute(rightIndex)))
                    .create(models[leftIndex].model, models[rightIndex].model)
            }
        return SimulatorScenario(
            "A → B ($transformationCount)",
            models,
            transformations.toSet(),
            listOf(
                changeSetOf(
                    AttributeSetChange(
                        models[0],
                        models[0].objects.first(),
                        value,
                        "a"
                    )
                )
            )
        )
    }

    fun char(index: Int) = (97 + index).toChar()
}

