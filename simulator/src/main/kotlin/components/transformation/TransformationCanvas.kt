package de.joshuagleitze.transformationnetwork.simulator.components.transformation

import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation
import de.joshuagleitze.transformationnetwork.simulator.components.arrow.ArrowTarget
import de.joshuagleitze.transformationnetwork.simulator.components.svg.SvgDefReceiver
import de.joshuagleitze.transformationnetwork.simulator.util.geometry.Coordinate
import kotlinext.js.jsObject
import kotlinx.css.Position.absolute
import kotlinx.css.height
import kotlinx.css.left
import kotlinx.css.pct
import kotlinx.css.position
import kotlinx.css.px
import kotlinx.css.top
import kotlinx.css.width
import kotlinx.css.zIndex
import org.w3c.dom.svg.SVGElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.RReadableRef
import react.RState
import react.createRef
import react.key
import react.setState
import styled.StyleSheet
import styled.css
import styled.styledSvg

private object TransformationCanvasStyles : StyleSheet("TransformationCanvas") {
    val transformationCanvas by css {
        position = absolute
        top = 0.px
        left = 0.px
        width = 100.pct
        height = 100.pct
        zIndex = 1
    }
}

interface TransformationCanvasProps : RProps {
    var modelArrowTargetProvider: (Model) -> ArrowTarget
    var transformations: Set<ModelTransformation>
}

private interface TransformationState : RState {
    var svgElement: RReadableRef<SVGElement>
    var coordinateSystem: SvgCoordinateSystem
}

private class TransformationCanvas : RComponent<TransformationCanvasProps, TransformationState>() {
    init {
        state = jsObject {
            svgElement = createRef()
            coordinateSystem = DefaultSvgCoordinateSystem(Coordinate.Zero)
        }
    }

    override fun componentDidMount() {
        val coordinateSystem = DefaultSvgCoordinateSystem.ofSvgElement(state.svgElement.current!!)
        if (coordinateSystem != state.coordinateSystem) setState { this.coordinateSystem = coordinateSystem }
    }

    override fun RBuilder.render() {
        styledSvg {
            ref = state.svgElement

            css { +TransformationCanvasStyles.transformationCanvas }

            SvgDefReceiver {
                /*
            defs {
                filter("doubleStroke") {
                    feMorphology(`in` = SourceGraphic, result = "a", operator = dilate, radius = "6")
                    feComposite(`in` = SourceGraphic, in2 = "a", operator = xor, result = "xx")
                }
    */

                props.transformations.forEach { transformation ->
                    TransformationView(transformation, state.coordinateSystem, props.modelArrowTargetProvider) {
                        attrs.key = transformation.hashCode().toString()
                    }
                }
            }
        }
    }
}

fun RBuilder.TransformationCanvas(
    modelArrowTargetProvider: (Model) -> ArrowTarget,
    transformations: Set<ModelTransformation>
) = child(TransformationCanvas::class) {
    attrs.modelArrowTargetProvider = modelArrowTargetProvider
    attrs.transformations = transformations
}