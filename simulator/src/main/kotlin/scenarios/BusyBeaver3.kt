package de.joshuagleitze.transformationnetwork.simulator.scenarios

import de.joshuagleitze.transformationnetwork.changemetamodel.AttributeSetChange
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.changeSetOf
import de.joshuagleitze.transformationnetwork.changerecording.factory.model
import de.joshuagleitze.transformationnetwork.models.turingmachine.TuringMachineMetamodel
import de.joshuagleitze.transformationnetwork.models.turingmachine.TuringState
import de.joshuagleitze.transformationnetwork.models.turingmachine.withInitialState
import de.joshuagleitze.transformationnetwork.simulator.data.scenario.SimulatorScenario
import de.joshuagleitze.transformationnetwork.simulator.data.scenario.at
import de.joshuagleitze.transformationnetwork.simulator.util.geometry.x
import de.joshuagleitze.transformationnetwork.transformations.busybeaver3.Q0Q1
import de.joshuagleitze.transformationnetwork.transformations.busybeaver3.Q0Qf
import de.joshuagleitze.transformationnetwork.transformations.busybeaver3.Q1Q2
import de.joshuagleitze.transformationnetwork.transformations.busybeaver3.Q2Q0

object BusyBeaver3 {
    fun create(): SimulatorScenario {
        val q0 = TuringMachineMetamodel.model("q0").withInitialState("0")
        val q1 = TuringMachineMetamodel.model("q1").withInitialState("0")
        val q2 = TuringMachineMetamodel.model("q2").withInitialState("0")
        val qf = TuringMachineMetamodel.model("qf").withInitialState("0")
        return SimulatorScenario(
            "3-State Busy Beaver",
            models = listOf(
                q0 at (1 x 1),
                q1 at (1 x 2),
                qf at (2 x 1),
                q2 at (2 x 2)
            ),
            transformations = setOf(
                Q0Q1.create(q0, q1),
                Q1Q2.create(q1, q2),
                Q2Q0.create(q2, q0),
                Q0Qf.create(q0, qf)
            ),
            changes = listOf(
                changeSetOf(
                    AttributeSetChange(q0, q0.objects.first(), TuringState.Metaclass.Attributes.timestamp, 1)
                )
            )
        )
    }
}
