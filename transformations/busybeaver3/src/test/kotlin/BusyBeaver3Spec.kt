import de.joshuagleitze.transformationnetwork.changerecording.factory.model
import de.joshuagleitze.transformationnetwork.metametamodel.ofType
import de.joshuagleitze.transformationnetwork.models.turingmachine.TuringMachineMetamodel
import de.joshuagleitze.transformationnetwork.models.turingmachine.TuringState
import de.joshuagleitze.transformationnetwork.models.turingmachine.withInitialState
import de.joshuagleitze.transformationnetwork.network.DefaultTransformationNetwork
import de.joshuagleitze.transformationnetwork.network.strategies.UnboundedPropagation
import de.joshuagleitze.transformationnetwork.transformations.busybeaver3.Q0Q1
import de.joshuagleitze.transformationnetwork.transformations.busybeaver3.Q0Qf
import de.joshuagleitze.transformationnetwork.transformations.busybeaver3.Q1Q2
import de.joshuagleitze.transformationnetwork.transformations.busybeaver3.Q2Q0
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BusyBeaver3Spec {
    private val q0Model = TuringMachineMetamodel.model("q0").withInitialState("0")
    private val q1Model = TuringMachineMetamodel.model("q1").withInitialState("0")
    private val q2Model = TuringMachineMetamodel.model("q2").withInitialState("0")
    private val qfModel = TuringMachineMetamodel.model("qf").withInitialState("0")
    private val network = DefaultTransformationNetwork(
        models = listOf(q0Model, q1Model, q2Model, qfModel),
        transformations = setOf(
            Q0Q1.create(q0Model, q1Model),
            Q1Q2.create(q1Model, q2Model),
            Q2Q0.create(q2Model, q0Model),
            Q0Qf.create(q0Model, qfModel)
        )
    )

    @Test
    fun effectiveBandContent() {
        UnboundedPropagation().preparePropagation(q0.recordChanges { q0.timestamp = 1 }, network).propagateAll()
        assertEquals("111111", qf.band)
    }

    @Test
    fun endTimestamp() {
        UnboundedPropagation().preparePropagation(q0.recordChanges { q0.timestamp = 1 }, network).propagateAll()
        assertEquals(11, qf.timestamp)
    }

    @Test
    fun endPosition() {
        UnboundedPropagation().preparePropagation(q0.recordChanges { q0.timestamp = 1 }, network).propagateAll()
        assertEquals(3, qf.bandPosition)
    }

    @Test
    fun endsInConsistentState() {
        UnboundedPropagation().preparePropagation(q0.recordChanges { q0.timestamp = 1 }, network).propagateAll()
        network.transformations.forEach {
            assertTrue(it.isConsistent())
        }
    }

    private val q0 get() = q0Model.objects.ofType(TuringState.Metaclass).first()
    private val qf get() = qfModel.objects.ofType(TuringState.Metaclass).first()
}
