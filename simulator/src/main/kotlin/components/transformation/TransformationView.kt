package de.joshuagleitze.transformationnetwork.simulator.components.transformation

import de.joshuagleitze.transformationnetwork.changerecording.ObservableModelTransformation
import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.simulator.components.arrow.ArrowTarget
import de.joshuagleitze.transformationnetwork.simulator.components.simulator.time
import de.joshuagleitze.transformationnetwork.simulator.components.svg.AppendDef
import de.joshuagleitze.transformationnetwork.simulator.data.arrow.ArrowTargetData
import de.joshuagleitze.transformationnetwork.simulator.data.arrow.lineBetweenWithMid
import de.joshuagleitze.transformationnetwork.simulator.styles.Colors
import de.joshuagleitze.transformationnetwork.simulator.styles.Dimension.baseSpacingPx
import de.joshuagleitze.transformationnetwork.simulator.styles.FontSize
import de.joshuagleitze.transformationnetwork.simulator.util.geometry.Coordinate
import de.joshuagleitze.transformationnetwork.simulator.util.svg.elementdsl.marker
import de.joshuagleitze.transformationnetwork.simulator.util.svg.elementdsl.styledCircle
import de.joshuagleitze.transformationnetwork.simulator.util.svg.elementdsl.styledLine
import de.joshuagleitze.transformationnetwork.simulator.util.svg.elementdsl.styledPath
import de.joshuagleitze.transformationnetwork.simulator.util.svg.elementdsl.styledPolyLine
import de.joshuagleitze.transformationnetwork.simulator.util.svg.elementdsl.use
import kotlinext.js.jsObject
import react.RBuilder
import react.RComponent
import react.RContext
import react.RHandler
import react.RProps
import react.RState
import react.RStatics
import react.setState
import styled.StyleSheet
import styled.css

private const val arrowHeadMarkerSize = 3.5
private const val conistencyMarkerSize = 3
private const val strokeWidth = FontSize.normalPx / 2
private val arrowEndDistance = 2 * baseSpacingPx
private val consistentState = "consistent"
private val inconsistentState = "inconsistent"

private object TransformationStyles : StyleSheet("Transformation") {
    val transformation by css {
        put("stroke", Colors.transformation.value)
        put("stroke-width", "$strokeWidth")
        put("fill", "none")
        put("marker-start", "url(#transformationArrowHeadStart)")
        put("marker-end", "url(#transformationArrowHeadEnd)")
        "&[data-state=\"$consistentState\"]" {
            put("marker-mid", "url(#transformationConsistent)")
        }
        "&[data-state=\"$inconsistentState\"]" {
            put("marker-mid", "url(#transformationInconsistent)")
        }
    }
    val executedTransformation by css {
        put("stroke", Colors.executed.value)
        put("marker-start", "url(#executedTransformationArrowHeadStart)")
        put("marker-end", "url(#executedTransformationArrowHeadEnd)")
        "&[data-state=\"$consistentState\"]" {
            put("marker-mid", "url(#executedTransformationConsistent)")
        }
        "&[data-state=\"$inconsistentState\"]" {
            put("marker-mid", "url(#executedTransformationInconsistent)")
        }
    }
    val transformationMarker by css {
        put("fill", Colors.transformation.value)
    }
    val executedTransformationMarker by css {
        put("fill", Colors.executed.value)
    }
    val marker = mapOf(
        "transformation" to transformationMarker,
        "executedTransformation" to executedTransformationMarker
    )
    val consistencyMarkerForeground by css {
        put("stroke", Colors.background.value)
        put("stroke-width", "2.3")
        put("stroke-linecap", "round")
        put("stroke-linejoin", "round")
        put("fill", "none")
    }
}

interface TransformationViewProps : RProps {
    var transformation: ObservableModelTransformation
    var coordinateSystem: SvgCoordinateSystem
    var modelArrowTargetProvider: (Model) -> ArrowTarget
}

private interface TransformationViewState : RState {
    var leftModelTarget: ArrowTargetData?
    var rightModelTarget: ArrowTargetData?
    var lastExecution: Int
}

private class TransformationView : RComponent<TransformationViewProps, TransformationViewState>() {
    lateinit var leftModelTarget: ArrowTarget
    lateinit var rightModelTarget: ArrowTarget
    private var lastConsistencyCheck = -1
    private var lastConsistencyResult = true

    init {
        state = jsObject {
            leftModelTarget = null
            rightModelTarget = null
            lastExecution = -1
        }
    }

    private val currentTime: Int get() = this.asDynamic().context as Int

    override fun RBuilder.render() {
        AppendDef("transformation-markers") {
            styledPath(id = "transformationArrowHeadPath", d = "M0,-3 L0,3 L6,0 z") {
                css { +TransformationStyles.transformationMarker }
            }
            styledPath(id = "executedTransformationArrowHeadPath", d = "M0,-3 L0,3 L6,0 z") {
                css { +TransformationStyles.executedTransformationMarker }
            }

            for (type in arrayOf("transformation", "executedTransformation")) {
                marker(
                    id = "${type}ArrowHeadStart",
                    markerWidth = "$arrowHeadMarkerSize",
                    markerHeight = "$arrowHeadMarkerSize",
                    refX = "6",
                    refY = "0",
                    orient = "auto",
                    viewBox = "0 -3 6 6"
                ) {
                    use(href = "#${type}ArrowHeadPath", transform = "rotate(180 3 0)")
                }

                marker(
                    id = "${type}ArrowHeadEnd",
                    markerWidth = "$arrowHeadMarkerSize",
                    markerHeight = "$arrowHeadMarkerSize",
                    refX = "0",
                    refY = "0",
                    orient = "auto",
                    viewBox = "0 -3 6 6"
                ) {
                    use(href = "#${type}ArrowHeadPath")
                }

                marker(
                    id = "${type}Consistent",
                    markerWidth = "$conistencyMarkerSize",
                    markerHeight = "$conistencyMarkerSize",
                    refX = "10",
                    refY = "10",
                    orient = "0",
                    viewBox = "0 0 20 20"
                ) {
                    styledCircle(c = Coordinate(10, 10), r = 10.0) {
                        css { +(TransformationStyles.marker[type] ?: error("No such marker style: $type")) }
                    }
                    styledPath(
                        d = "M 5.1,10.5 8,13.4 14.8,6.7"
                    ) {
                        css { +(TransformationStyles.consistencyMarkerForeground) }
                    }
                }

                marker(
                    id = "${type}Inconsistent",
                    markerWidth = "$conistencyMarkerSize",
                    markerHeight = "$conistencyMarkerSize",
                    refX = "10",
                    refY = "10",
                    orient = "0",
                    viewBox = "0 0 20 20"
                ) {
                    styledCircle(c = Coordinate(10, 10), r = 10.0) {
                        css { +(TransformationStyles.marker[type] ?: error("No such marker style: $type")) }
                    }
                    styledLine(Coordinate(6, 6), Coordinate(14, 14)) {
                        css { +(TransformationStyles.consistencyMarkerForeground) }
                    }
                    styledLine(Coordinate(6, 14), Coordinate(14, 6)) {
                        css { +(TransformationStyles.consistencyMarkerForeground) }
                    }
                }
            }
        }

        computeLine()?.let { line ->
            styledPolyLine(line) {
                attrs.attributes["data-state"] =
                    if (transformationIsConsistent) consistentState
                    else inconsistentState
                css {
                    +TransformationStyles.transformation
                    if (state.lastExecution == currentTime) +TransformationStyles.executedTransformation
                }
            }
        }
    }

    private val onExecution = { _: Unit ->
        if (state.lastExecution != currentTime) setState { lastExecution = currentTime }
    }

    override fun componentDidMount() {
        leftModelTarget = props.modelArrowTargetProvider(props.transformation.leftModel)
        leftModelTarget.data.subscribe(this.setLeftModelArrowTargetData)

        rightModelTarget = props.modelArrowTargetProvider(props.transformation.rightModel)
        rightModelTarget.data.subscribe(this.setRightModelArrowTargetData)

        props.transformation.execution.subscribe(onExecution)

        setState {
            this.leftModelTarget = this@TransformationView.leftModelTarget.data.last
            this.rightModelTarget = this@TransformationView.rightModelTarget.data.last
        }
    }

    override fun componentWillUnmount() {
        leftModelTarget.data.unsubscribe(this.setLeftModelArrowTargetData)
        rightModelTarget.data.unsubscribe(this.setRightModelArrowTargetData)

        props.transformation.execution.unsubscribe(onExecution)
    }

    private val setLeftModelArrowTargetData = { data: ArrowTargetData? ->
        setState { leftModelTarget = data }
    }

    private val setRightModelArrowTargetData = { data: ArrowTargetData? ->
        setState { rightModelTarget = data }
    }

    private fun computeLine() = state.leftModelTarget?.let { leftModelTarget ->
        state.rightModelTarget?.let { rightModelTarget ->
            val distance = arrowEndDistance + arrowHeadMarkerSize * strokeWidth
            lineBetweenWithMid(leftModelTarget, rightModelTarget, distance)
                .transformCoordinates { props.coordinateSystem.domCoordinateToSvg(it) }
        }
    }

    private val transformationIsConsistent: Boolean
        get() {
            if (currentTime > lastConsistencyCheck) {
                lastConsistencyResult = props.transformation.isConsistent()
            }
            return lastConsistencyResult
        }

    companion object :
        RStatics<TransformationViewProps, TransformationViewState, TransformationView, RContext<Any>>(
            TransformationView::class
        ) {
        init {
            TransformationView.contextType = time.unsafeCast<RContext<Any>>()
        }
    }
}

fun RBuilder.TransformationView(
    transformation: ObservableModelTransformation,
    coordinateSystem: SvgCoordinateSystem,
    modelArrowTargetProvider: (Model) -> ArrowTarget,
    handler: RHandler<TransformationViewProps> = {}
) = child(TransformationView::class) {
    attrs.transformation = transformation
    attrs.coordinateSystem = coordinateSystem
    attrs.modelArrowTargetProvider = modelArrowTargetProvider
    handler()
}
