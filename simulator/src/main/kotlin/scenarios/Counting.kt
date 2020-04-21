package de.joshuagleitze.transformationnetwork.simulator.scenarios

import Incrementing
import de.joshuagleitze.transformationnetwork.changemetamodel.AttributeSetChange
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.changeSetOf
import de.joshuagleitze.transformationnetwork.changerecording.factory.model
import de.joshuagleitze.transformationnetwork.models.primitives.Number.Metaclass.Attributes.value
import de.joshuagleitze.transformationnetwork.models.primitives.PrimitivesMetamodel
import de.joshuagleitze.transformationnetwork.models.primitives.withNumber
import de.joshuagleitze.transformationnetwork.simulator.data.scenario.SimulatorScenario
import de.joshuagleitze.transformationnetwork.simulator.data.scenario.at
import de.joshuagleitze.transformationnetwork.simulator.util.geometry.x

object Counting {
    fun create(): SimulatorScenario {
        val center = PrimitivesMetamodel.model("Z").withNumber(0)
        val ten1 = PrimitivesMetamodel.model("10/1").withNumber(0)
        val ten2 = PrimitivesMetamodel.model("10/2").withNumber(0)
        val hundred1 = PrimitivesMetamodel.model("100/1").withNumber(0)
        val hundred2 = PrimitivesMetamodel.model("100/2").withNumber(0)
        val thousand1 = PrimitivesMetamodel.model("1000/1").withNumber(0)
        val thousand2 = PrimitivesMetamodel.model("1000/2").withNumber(0)
        return SimulatorScenario(
            "Counting",
            models = listOf(
                ten1 at (1 x 1),
                ten2 at (2 x 1),
                center at (3 x 2),
                hundred1 at (4 x 1),
                hundred2 at (5 x 1),
                thousand1 at (4 x 3),
                thousand2 at (2 x 3)
            ),
            transformations = setOf(
                Incrementing.between(0, 9).create(ten1, ten2),
                Incrementing.between(0, 9).create(ten2, center),
                Incrementing.between(0, 9).create(center, ten1),
                Incrementing.between(10, 99).create(hundred1, hundred2),
                Incrementing.between(10, 99).create(hundred2, center),
                Incrementing.between(10, 99).create(center, hundred1),
                Incrementing.between(100, 999).create(thousand1, thousand2),
                Incrementing.between(100, 999).create(thousand2, center),
                Incrementing.between(100, 999).create(center, thousand1)
            ),
            changes = listOf(
                changeSetOf(
                    AttributeSetChange(center, center.objects.first(), value, 1)
                )
            )
        )
    }
}
