package de.joshuagleitze.transformationnetwork.simulator.simulator

import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation
import de.joshuagleitze.transformationnetwork.simulator.AppendDef
import de.joshuagleitze.transformationnetwork.simulator.ArrowTarget
import de.joshuagleitze.transformationnetwork.simulator.ArrowTargetData
import de.joshuagleitze.transformationnetwork.simulator.Coordinate
import de.joshuagleitze.transformationnetwork.simulator.lineBetween
import de.joshuagleitze.transformationnetwork.simulator.styles.Colors.transformationColor
import de.joshuagleitze.transformationnetwork.simulator.styles.Dimension.baseSpacingPx
import de.joshuagleitze.transformationnetwork.simulator.styles.FontSize
import de.joshuagleitze.transformationnetwork.simulator.svgdsl.marker
import de.joshuagleitze.transformationnetwork.simulator.svgdsl.styledLine
import de.joshuagleitze.transformationnetwork.simulator.svgdsl.styledPath
import de.joshuagleitze.transformationnetwork.simulator.svgdsl.use
import kotlinext.js.jsObject
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.setState
import styled.StyleSheet
import styled.css

private const val markerSize = 3.5
private const val strokeWidth = FontSize.normalPx / 2
private val arrowEndDistance = 2 * baseSpacingPx

private object TransformationStyles : StyleSheet("TransformationStyles") {
    val transformation by css {
        put("stroke", transformationColor.value)
        put("stroke-width", "$strokeWidth")
        put("fill", "none")
        put("marker-start", "url(#transformationArrowHeadStart)")
        put("marker-end", "url(#transformationArrowHeadEnd)")
    }
    val transformationArrowTip by css {
        put("fill", transformationColor.value)
    }
}

interface TransformationViewProps : RProps {
    var transformation: ModelTransformation
    var coordinateOrigin: Coordinate
    var modelArrowTargetProvider: (Model) -> ArrowTarget
}

private interface TransformationViewState : RState {
    var leftModelTarget: ArrowTargetData?
    var rightModelTarget: ArrowTargetData?
}

private class TransformationView : RComponent<TransformationViewProps, TransformationViewState>() {
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
        val leftModelTarget = props.modelArrowTargetProvider(props.transformation.leftModel)
        leftModelTarget.data.subscribe(this::setLeftModelArrowTargetData)

        val rightModelTarget = props.modelArrowTargetProvider(props.transformation.rightModel)
        rightModelTarget.data.subscribe(this::setRightModelArrowTargetData)

        setState {
            this.leftModelTarget = leftModelTarget.data.current
            this.rightModelTarget = rightModelTarget.data.current
        }
    }

    override fun componentWillUnmount() {
        val leftModelTarget = props.modelArrowTargetProvider(props.transformation.leftModel)
        leftModelTarget.data.unsubscribe(this::setLeftModelArrowTargetData)

        val rightModelTarget = props.modelArrowTargetProvider(props.transformation.rightModel)
        rightModelTarget.data.unsubscribe(this::setRightModelArrowTargetData)
    }

    private fun setLeftModelArrowTargetData(data: ArrowTargetData?) {
        setState { leftModelTarget = data }
    }

    private fun setRightModelArrowTargetData(data: ArrowTargetData?) {
        setState { rightModelTarget = data }
    }

    private fun computeLine() = state.leftModelTarget?.let { leftModelTarget ->
        state.rightModelTarget?.let { rightModelTarget ->
            val distance = arrowEndDistance + markerSize * strokeWidth
            lineBetween(leftModelTarget, rightModelTarget, distance) - props.coordinateOrigin
        }
    }
}

fun RBuilder.TransformationView(
    transformation: ModelTransformation,
    coordinateOrigin: Coordinate,
    modelArrowTargetProvider: (Model) -> ArrowTarget
) = child(TransformationView::class) {
    attrs.transformation = transformation
    attrs.coordinateOrigin = coordinateOrigin
    attrs.modelArrowTargetProvider = modelArrowTargetProvider
}