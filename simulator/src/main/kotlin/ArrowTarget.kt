package de.joshuagleitze.transformationnetwork.simulator

import de.joshuagleitze.transformationnetwork.simulator.Angle.Companion.PI
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import react.RBuilder
import react.RComponent
import react.RHandler
import react.RProps
import react.RReadableRef
import react.RState
import kotlin.browser.window

data class CornerRoundings(
    val topLeft: Double,
    val topRight: Double,
    val bottomRight: Double,
    val bottomLeft: Double
) {
    companion object {
        val ZERO = CornerRoundings(.0, .0, .0, .0)
    }
}

interface ArrowTargetData {
    val width: Double
    val height: Double
    val left: Double
    val right: Double
    val top: Double
    val bottom: Double
    val cornerRounding: CornerRoundings

    val center: Coordinate
    val topLeft: Coordinate
    val topRight: Coordinate
    val bottomLeft: Coordinate
    val bottomRight: Coordinate

    fun projectToBorder(angle: Angle, distance: Double = .0): Coordinate
}

class DomArrowTargetData(element: HTMLElement) : ArrowTargetData {
    override val topLeft: Coordinate
    override val bottomRight: Coordinate

    init {
        val outerRect = element.getBoundingClientRect()
        topLeft = Coordinate(outerRect.left, outerRect.top)
        bottomRight = Coordinate(outerRect.right, outerRect.bottom)
    }

    override val left: Double get() = topLeft.x
    override val right: Double get() = bottomRight.x
    override val top: Double get() = topLeft.y
    override val bottom: Double get() = bottomRight.y
    override val center get() = (topLeft + bottomRight) / 2
    override val width get() = right - left
    override val height get() = bottom - top
    override val cornerRounding get() = CornerRoundings.ZERO
    override val topRight get() = Coordinate(right, top)
    override val bottomLeft get() = Coordinate(left, bottom)

    override fun projectToBorder(angle: Angle, distance: Double) = when (angle) {
        in -(PI / 4)..(PI / 4) -> Coordinate(right, center.y - angle.tangent() / right)
        in (PI / 4)..(3 * PI / 4) -> Coordinate(center.x - (angle - PI / 2).tangent() / top, top)
        in (3 * PI / 4)..-(3 * PI / 4) -> Coordinate(left, center.y + (angle - PI).tangent() / left)
        else -> Coordinate(center.x + (angle + PI / 2).tangent() / bottom, bottom)
    }.let { pointOnBorder ->
        pointOnBorder + (pointOnBorder - center).normalize() * distance
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as DomArrowTargetData

        if (topLeft != other.topLeft) return false
        if (bottomRight != other.bottomRight) return false

        return true
    }

    override fun hashCode(): Int {
        var result = topLeft.hashCode()
        result = 31 * result + bottomRight.hashCode()
        return result
    }
}

interface ArrowTargetProps : RProps {
    var targetRef: RReadableRef<HTMLElement>
}

interface ArrowTarget {
    val data: Observable<ArrowTargetData?>
}

private class ArrowTargetComponent private constructor() : RComponent<ArrowTargetProps, RState>(), ArrowTarget {
    override var data = PublishingObservable<ArrowTargetData?>(null)
    private lateinit var targetContainer: HTMLElement

    override fun RBuilder.render() {
        props.children()
    }

    @Suppress("UNUSED_PARAMETER") // used to make a method reference for event listeners possible
    private fun publishData(causingEvent: Event? = null) {
        this.data.publishIfChanged(DomArrowTargetData(targetContainer))
    }

    override fun componentDidMount() {
        targetContainer = props.targetRef.current!!
        publishData()
        window.addEventListener("resize", this::publishData)
    }

    override fun componentWillUnmount() {
        window.removeEventListener("resize", this::publishData)
    }
}

fun RBuilder.ArrowTarget(targetRef: RReadableRef<HTMLElement>, handler: RHandler<ArrowTargetProps>) =
    child(ArrowTargetComponent::class) {
        attrs.targetRef = targetRef
        handler()
    }

fun lineBetween(start: ArrowTargetData, end: ArrowTargetData, spacing: Double = .0): Line {
    val angle = Angle.between(start.center, end.center)
    val startCoordinate = start.projectToBorder(angle, spacing)
    val endCoordinate = end.projectToBorder(-angle, spacing)
    return Line(startCoordinate, endCoordinate)
}