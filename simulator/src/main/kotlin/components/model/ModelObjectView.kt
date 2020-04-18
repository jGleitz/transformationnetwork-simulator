package de.joshuagleitze.transformationnetwork.simulator.components.model

import de.joshuagleitze.transformationnetwork.changemetamodel.AnyAttributeSetChange
import de.joshuagleitze.transformationnetwork.changerecording.AnyChangeRecordingModelObject
import de.joshuagleitze.transformationnetwork.metametamodel.AnyModelObjectIdentifier
import de.joshuagleitze.transformationnetwork.metametamodel.MetaAttribute
import de.joshuagleitze.transformationnetwork.simulator.components.simulator.time
import de.joshuagleitze.transformationnetwork.simulator.styles.Colors
import de.joshuagleitze.transformationnetwork.simulator.styles.Dimension.baseSpacing
import de.joshuagleitze.transformationnetwork.simulator.styles.Dimension.controlCornerRounding
import de.joshuagleitze.transformationnetwork.simulator.styles.FontSize
import de.joshuagleitze.transformationnetwork.simulator.util.DataChangeEvent
import kotlinext.js.jsObject
import kotlinx.css.BorderCollapse.collapse
import kotlinx.css.BorderStyle.solid
import kotlinx.css.Display.inlineBlock
import kotlinx.css.FontStyle.Companion.italic
import kotlinx.css.Position.absolute
import kotlinx.css.Position.relative
import kotlinx.css.TextAlign
import kotlinx.css.TextAlign.center
import kotlinx.css.backgroundColor
import kotlinx.css.borderCollapse
import kotlinx.css.borderRadius
import kotlinx.css.bottom
import kotlinx.css.content
import kotlinx.css.display
import kotlinx.css.ex
import kotlinx.css.fontSize
import kotlinx.css.fontStyle
import kotlinx.css.height
import kotlinx.css.left
import kotlinx.css.marginLeft
import kotlinx.css.marginRight
import kotlinx.css.marginTop
import kotlinx.css.minWidth
import kotlinx.css.padding
import kotlinx.css.paddingBottom
import kotlinx.css.paddingTop
import kotlinx.css.pct
import kotlinx.css.position
import kotlinx.css.properties.TextDecoration
import kotlinx.css.properties.TextDecorationLine.underline
import kotlinx.css.properties.borderTop
import kotlinx.css.px
import kotlinx.css.quoted
import kotlinx.css.right
import kotlinx.css.textAlign
import kotlinx.css.textDecoration
import kotlinx.css.top
import kotlinx.css.width
import kotlinx.css.zIndex
import kotlinx.html.js.onMouseOutFunction
import kotlinx.html.js.onMouseOverFunction
import org.w3c.dom.HTMLElement
import react.RBuilder
import react.RComponent
import react.RContext
import react.RHandler
import react.RProps
import react.RReadableRef
import react.RState
import react.RStatics
import react.createRef
import react.dom.span
import react.dom.tbody
import react.setState
import styled.StyleSheet
import styled.css
import styled.styledDiv
import styled.styledSpan
import styled.styledTable
import styled.styledTd
import styled.styledTr
import kotlin.js.Date

private object ModelObjectStyles : StyleSheet("ModelObject") {
    val highlighted by css {
        backgroundColor = Colors.highlighted
    }
    val typeHeader by css {
        width = 100.pct
        fontSize = FontSize.small
        marginTop = baseSpacing
        borderTop(1.px, solid, Colors.border)
        paddingTop = .5.ex
        paddingBottom = .2.ex
        textAlign = center
    }
    val attributesTable by css {
        borderCollapse = collapse
        minWidth = 100.pct
        fontSize = FontSize.small
    }
    val attributeRow by css {
        position = relative
        zIndex = 1
        nthChild("even") {
            after {
                position = absolute
                top = 0.px
                left = 0.px
                width = 100.pct
                height = 100.pct
                backgroundColor = Colors.alternativeBackground
                zIndex = -1
            }
        }
    }
    val addedTable by css {
        backgroundColor = Colors.added
    }
    val updatedCell by css {
        position = relative
        before {
            position = absolute
            borderRadius = controlCornerRounding
            top = 2.px
            left = 0.px
            right = 2.px
            bottom = 2.px
            backgroundColor = Colors.updated
            zIndex = -2
        }
    }
    private val trTopBottomSpacing = baseSpacing * 0.5
    private val trInnerSpacing = baseSpacing * 0.5
    private val trOuterSpacing = baseSpacing * 1.5
    val attributeName by css {
        textAlign = TextAlign.right
        padding(
            top = trTopBottomSpacing,
            bottom = trTopBottomSpacing,
            left = trOuterSpacing,
            right = trInnerSpacing
        )
    }
    val attributeValue by css {
        padding(
            top = trTopBottomSpacing,
            bottom = trTopBottomSpacing,
            left = trInnerSpacing,
            right = trOuterSpacing
        )
    }
    val nullValue by css {
        fontStyle = italic
    }
    val emptyListValue by css {
        before {
            content = "[".quoted
            display = inlineBlock
            marginRight = .2.ex
        }
        after {
            content = "]".quoted
            display = inlineBlock
            marginLeft = .2.ex
        }
    }
    val singleElementListValue by css {
        before {
            content = "[".quoted
            display = inlineBlock
            marginRight = .5.ex
        }
        after {
            content = "]".quoted
            display = inlineBlock
            marginLeft = .5.ex
        }
    }
    val listValue by css {
        before {
            content = "[".quoted
            display = inlineBlock
            marginRight = .5.ex
        }
        after {
            content = "]".quoted
            display = inlineBlock
            marginLeft = .5.ex
        }
    }
    val listValueElement by css {
        after {
            content = ", ".quoted
        }
        lastChild {
            after {
                content = "".quoted
            }
        }
    }
    val referenceValue by css {
        hover {
            textDecoration = TextDecoration(setOf(underline))
        }
        before {
            content = "→ ".quoted
        }
    }
}

interface ModelObjectViewProps : RProps {
    var modelObject: AnyChangeRecordingModelObject
    var addedTime: Int?
    var highlighted: Boolean
    var highlighter: (AnyModelObjectIdentifier?) -> Unit
}

private interface ModelObjectViewState : RState {
    var lastUpdate: Int
    var lastUpdatedAttributes: List<MetaAttribute<*>>
    var valuesTableRef: RReadableRef<HTMLElement>
}

private class ModelObjectView : RComponent<ModelObjectViewProps, ModelObjectViewState>() {
    private val currentTime: Int get() = this.asDynamic().context as Int
    private var subscribed = false

    init {
        state = jsObject {
            valuesTableRef = createRef()
            lastUpdate = 0
            lastUpdatedAttributes = emptyList()
        }
    }

    override fun RBuilder.render() {
        styledDiv {
            css {
                +ModelObjectStyles.typeHeader
                if (props.highlighted) +ModelObjectStyles.highlighted
            }
            +props.modelObject.metaclass.name
        }
        styledTable {
            ref = state.valuesTableRef
            css {
                +ModelObjectStyles.attributesTable
                if (currentTime == props.addedTime) +ModelObjectStyles.addedTable
                if (props.highlighted) +ModelObjectStyles.highlighted
            }

            tbody {
                props.modelObject.metaclass.attributes.forEach { attribute ->
                    styledTr {
                        css { +ModelObjectStyles.attributeRow }
                        styledTd {
                            css { +ModelObjectStyles.attributeName }
                            +attribute.name
                            +":"
                        }
                        styledTd {
                            css {
                                +ModelObjectStyles.attributeValue
                                if (state.lastUpdate == currentTime
                                    && state.lastUpdatedAttributes.contains(attribute)
                                ) {
                                    +ModelObjectStyles.updatedCell
                                }
                            }
                            styledValue(props.modelObject[attribute])
                        }
                    }
                }
            }
        }
    }

    private val onAttributeChange = { change: AnyAttributeSetChange ->
        var previous = state.lastUpdatedAttributes
        if (state.lastUpdate != currentTime) {
            previous = emptyList()
        }
        state.valuesTableRef.current?.dispatchEvent(DataChangeEvent())
        setState {
            lastUpdate = currentTime
            lastUpdatedAttributes = previous + change.targetAttribute
        }
    }

    override fun componentDidMount() {
        this.props.subscribe()
    }

    override fun componentWillUnmount() {
        this.props.unsubscribe()
    }

    override fun componentDidUpdate(prevProps: ModelObjectViewProps, prevState: ModelObjectViewState, snapshot: Any) {
        if (prevProps.modelObject !== this.props.modelObject) {
            prevProps.unsubscribe()
            this.props.subscribe()
        }
    }

    private fun ModelObjectViewProps.subscribe() {
        check(!subscribed) { "already subscribed for $modelObject!" }
        console.log("subscribing on $modelObject")
        modelObject.directChanges.subscribe(onAttributeChange)
        subscribed = true
    }

    private fun ModelObjectViewProps.unsubscribe() {
        if (subscribed) {
            console.log("unsubscribing on $modelObject")
            modelObject.directChanges.unsubscribe(onAttributeChange)
        } else console.log("not subscribed yet for $modelObject!")
        subscribed = false
    }

    private fun RBuilder.styledValue(value: Any?) {
        when (value) {
            null -> nullValue()
            is List<*> -> listValue(value)
            is AnyModelObjectIdentifier -> referenceValue(value)
            is Date -> span { +value.toLocaleDateString() }
            else -> span { +value.toString() }
        }
    }

    private fun RBuilder.referenceValue(value: AnyModelObjectIdentifier) {
        styledSpan {
            css { +ModelObjectStyles.referenceValue }
            attrs.onMouseOverFunction = { props.highlighter(value) }
            attrs.onMouseOutFunction = { props.highlighter(null) }
            +value.toString()
        }
    }

    private fun RBuilder.listValue(value: List<Any?>) {
        styledSpan {
            css {
                +when (value.size) {
                    0 -> ModelObjectStyles.emptyListValue
                    1 -> ModelObjectStyles.singleElementListValue
                    else -> ModelObjectStyles.listValue
                }
            }
            for (element in value) {
                styledSpan {
                    css {
                        +ModelObjectStyles.listValueElement
                    }
                    styledValue(element)
                }
            }
        }
    }

    private fun RBuilder.nullValue() {
        styledSpan {
            css { +ModelObjectStyles.nullValue }
        }
    }

    companion object : RStatics<ModelObjectViewProps, ModelObjectViewState, ModelObjectView, RContext<Any>>(
        ModelObjectView::class
    ) {
        init {
            ModelObjectView.contextType = time.unsafeCast<RContext<Any>>()
        }
    }
}

fun RBuilder.ModelObjectView(
    modelObject: AnyChangeRecordingModelObject,
    addedTime: Int?,
    highlighted: Boolean = false,
    highlighter: (AnyModelObjectIdentifier?) -> Unit = {},
    handler: RHandler<ModelObjectViewProps> = {}
) = child(ModelObjectView::class) {
    attrs.modelObject = modelObject
    attrs.addedTime = addedTime
    attrs.highlighted = highlighted
    attrs.highlighter = highlighter
    handler()
}
