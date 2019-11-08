package de.joshuagleitze.transformationnetwork.simulator.view.transformation

import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.modeltransformation.ModelTransformation
import de.joshuagleitze.transformationnetwork.simulator.ArrowTarget
import de.joshuagleitze.transformationnetwork.simulator.Coordinate
import de.joshuagleitze.transformationnetwork.simulator.SvgDefReceiver
import de.joshuagleitze.transformationnetwork.simulator.simulator.TransformationView
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
import org.w3c.dom.HTMLElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.RReadableRef
import react.RState
import react.createRef
import react.setState
import styled.StyleSheet
import styled.css
import styled.styledSvg

private object TransformationCanvasStyles : StyleSheet("TransformationCanvasStyles") {
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
    var transformations: List<ModelTransformation>
}

private interface TransformationState : RState {
    var svgElement: RReadableRef<HTMLElement>
    var svgOrigin: Coordinate
}

private class TransformationCanvas : RComponent<TransformationCanvasProps, TransformationState>() {
    init {
        state = jsObject {
            svgElement = createRef()
            svgOrigin = Coordinate.Zero
        }
    }

    override fun componentDidMount() {
        state.svgElement.current!!.getBoundingClientRect().let { svgRect ->
            val origin = Coordinate(svgRect.left, svgRect.top)
            if (origin != state.svgOrigin) setState { svgOrigin = origin }
        }
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

                path(id = "transformationArrowHeadPath", d = "M0,0 L0,6 L9,3 z")
                marker(
                    id = "transformationArrowHeadStart",
                    markerWidth = "9",
                    markerHeight = "6",
                    refX = "9",
                    refY = "3",
                    orient = "auto"
                ) {
                    use(href = "#transformationArrowHeadPath", transform = "rotate(180 4.5 3)")
                }

                marker(
                    id = "transformationArrowHeadEnd",
                    markerWidth = "9",
                    markerHeight = "6",
                    refX = "0",
                    refY = "3",
                    orient = "auto"
                ) {
                    use(href = "#transformationArrowHeadPath")
                }
            }
 */

                props.transformations.forEach { transformation ->
                    TransformationView(transformation, state.svgOrigin, props.modelArrowTargetProvider)
                }
            }
        }
    }
}

fun RBuilder.TransformationCanvas(
    modelArrowTargetProvider: (Model) -> ArrowTarget,
    transformations: List<ModelTransformation>
) = child(TransformationCanvas::class) {
    attrs.modelArrowTargetProvider = modelArrowTargetProvider
    attrs.transformations = transformations
}