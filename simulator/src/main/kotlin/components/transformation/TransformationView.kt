package de.joshuagleitze.transformationnetwork.simulator.components.transformation

import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation
import de.joshuagleitze.transformationnetwork.simulator.components.arrow.ArrowTarget
import de.joshuagleitze.transformationnetwork.simulator.components.svg.AppendDef
import de.joshuagleitze.transformationnetwork.simulator.data.arrow.ArrowTargetData
import de.joshuagleitze.transformationnetwork.simulator.data.arrow.lineBetween
import de.joshuagleitze.transformationnetwork.simulator.styles.Colors
import de.joshuagleitze.transformationnetwork.simulator.styles.Dimension.baseSpacingPx
import de.joshuagleitze.transformationnetwork.simulator.styles.FontSize
import de.joshuagleitze.transformationnetwork.simulator.util.svg.elementdsl.marker
import de.joshuagleitze.transformationnetwork.simulator.util.svg.elementdsl.styledLine
import de.joshuagleitze.transformationnetwork.simulator.util.svg.elementdsl.styledPath
import de.joshuagleitze.transformationnetwork.simulator.util.svg.elementdsl.use
import kotlinext.js.jsObject
import react.RBuilder
import react.RComponent
import react.RHandler
import react.RProps
import react.RState
import react.setState
import styled.StyleSheet
import styled.css

private const val markerSize = 3.5
private const val strokeWidth = FontSize.normalPx / 2
private val arrowEndDistance = 2 * baseSpacingPx

private object TransformationStyles : StyleSheet("Transformation") {
    val transformation by css {
        put("stroke", Colors.transformation.value)
        put("stroke-width", "$strokeWidth")
        put("fill", "none")
        put("marker-start", "url(#transformationArrowHeadStart)")
        put("marker-end", "url(#transformationArrowHeadEnd)")
    }
    val transformationArrowTip by css {
        put("fill", Colors.transformation.value)
    }
}

interface TransformationViewProps : RProps {
    var transformation: ModelTransformation
    var coordinateSystem: SvgCoordinateSystem
    var modelArrowTargetProvider: (Model) -> ArrowTarget
}

private interface TransformationViewState : RState {
    var leftModelTarget: ArrowTargetData?
    var rightModelTarget: ArrowTargetData?
}

private class TransformationView : RComponent<TransformationViewProps, TransformationViewState>() {
    lateinit var leftModelTarget: ArrowTarget
    lateinit var rightModelTarget: ArrowTarget

    init {
        state = jsObject {
            leftModelTarget = null
            rightModelTarget = null
        }
    }

    override fun RBuilder.render() {
        AppendDef("transformation-markers") {
            styledPath(id = "transformationArrowHeadPath", d = "M0,-3 L0,3 L6,0 z") {
                css { +TransformationStyles.transformationArrowTip }
            }
            marker(
                id = "transformationArrowHeadStart",
                markerWidth = "$markerSize",
                markerHeight = "$markerSize",
                refX = "6",
                refY = "0",
                orient = "auto",
                viewBox = "0 -3 6 6"
            ) {
                use(href = "#transformationArrowHeadPath", transform = "rotate(180 3 0)")
            }

            marker(
                id = "transformationArrowHeadEnd",
                markerWidth = "$markerSize",
                markerHeight = "$markerSize",
                refX = "0",
                refY = "0",
                orient = "auto",
                viewBox = "0 -3 6 6"
            ) {
                use(href = "#transformationArrowHeadPath")
            }
        }

        computeLine()?.let { line ->
            styledLine(line) {
                css { +TransformationStyles.transformation }
            }
        }
    }

    override fun componentDidMount() {
        leftModelTarget = props.modelArrowTargetProvider(props.transformation.leftModel)
        leftModelTarget.data.subscribe(this.setLeftModelArrowTargetData)

        rightModelTarget = props.modelArrowTargetProvider(props.transformation.rightModel)
        rightModelTarget.data.subscribe(this.setRightModelArrowTargetData)

        setState {
            this.leftModelTarget = this@TransformationView.leftModelTarget.data.last
            this.rightModelTarget = this@TransformationView.rightModelTarget.data.last
        }
    }

    override fun componentWillUnmount() {
        leftModelTarget.data.unsubscribe(this.setLeftModelArrowTargetData)
        rightModelTarget.data.unsubscribe(this.setRightModelArrowTargetData)
    }

    private val setLeftModelArrowTargetData = { data: ArrowTargetData? ->
        setState { leftModelTarget = data }
    }

    private val setRightModelArrowTargetData = { data: ArrowTargetData? ->
        setState { rightModelTarget = data }
    }

    private fun computeLine() = state.leftModelTarget?.let { leftModelTarget ->
        state.rightModelTarget?.let { rightModelTarget ->
            val distance = arrowEndDistance + markerSize * strokeWidth
            lineBetween(leftModelTarget, rightModelTarget, distance)
                .transformCoordinates { props.coordinateSystem.domCoordinateToSvg(it) }
        }
    }
}

fun RBuilder.TransformationView(
    transformation: ModelTransformation,
    coordinateSystem: SvgCoordinateSystem,
    modelArrowTargetProvider: (Model) -> ArrowTarget,
    handler: RHandler<TransformationViewProps> = {}
) = child(TransformationView::class) {
    attrs.transformation = transformation
    attrs.coordinateSystem = coordinateSystem
    attrs.modelArrowTargetProvider = modelArrowTargetProvider
    handler()
}