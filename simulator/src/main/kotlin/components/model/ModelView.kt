package de.joshuagleitze.transformationnetwork.simulator.components.model

import de.joshuagleitze.transformationnetwork.changemetamodel.AdditionChange
import de.joshuagleitze.transformationnetwork.changemetamodel.AnyModelObjectChange
import de.joshuagleitze.transformationnetwork.metametamodel.AnyModelObjectIdentifier
import de.joshuagleitze.transformationnetwork.metametamodel.AnyModelObjectIdentity
import de.joshuagleitze.transformationnetwork.simulator.components.arrow.ArrowTarget
import de.joshuagleitze.transformationnetwork.simulator.components.simulator.time
import de.joshuagleitze.transformationnetwork.simulator.data.scenario.PositionedModel
import de.joshuagleitze.transformationnetwork.simulator.styles.Colors
import de.joshuagleitze.transformationnetwork.simulator.styles.Dimension.baseSpacing
import de.joshuagleitze.transformationnetwork.simulator.styles.FontSize
import de.joshuagleitze.transformationnetwork.simulator.styles.FontSize.large
import de.joshuagleitze.transformationnetwork.simulator.styles.FontSize.normal
import de.joshuagleitze.transformationnetwork.simulator.util.DataChangeEvent
import kotlinext.js.jsObject
import kotlinx.css.BorderStyle.solid
import kotlinx.css.Display.inlineBlock
import kotlinx.css.GridColumnStart
import kotlinx.css.GridRowStart
import kotlinx.css.LinearDimension.Companion.fitContent
import kotlinx.css.TextAlign.center
import kotlinx.css.borderColor
import kotlinx.css.borderRadius
import kotlinx.css.borderStyle
import kotlinx.css.borderWidth
import kotlinx.css.color
import kotlinx.css.display
import kotlinx.css.fontSize
import kotlinx.css.gridColumnStart
import kotlinx.css.gridRowStart
import kotlinx.css.margin
import kotlinx.css.marginTop
import kotlinx.css.minWidth
import kotlinx.css.padding
import kotlinx.css.pct
import kotlinx.css.px
import kotlinx.css.textAlign
import kotlinx.css.width
import org.w3c.dom.HTMLElement
import react.RBuilder
import react.RComponent
import react.RContext
import react.RHandler
import react.RProps
import react.RReadableRef
import react.RRef
import react.RState
import react.RStatics
import react.createRef
import react.forwardRef
import react.setState
import styled.StyleSheet
import styled.css
import styled.styledDiv
import styled.styledH3
import styled.styledSpan

@Suppress("RemoveRedundantQualifierName")
private object ModelStyles : StyleSheet("Model") {
    val modelContainer by css {
        padding(vertical = baseSpacing * 2, horizontal = baseSpacing * 1.5)
        borderStyle = solid
        borderWidth = 1.px
        borderColor = Colors.border
        borderRadius = FontSize.normal
        width = fitContent
    }
    val modelName by css {
        fontSize = large
        textAlign = center
        display = inlineBlock
        width = 100.pct
        margin(all = 0.px)
    }
    val metamodelName by css {
        fontSize = normal
        color = Colors.lessImportant
        textAlign = center
        display = inlineBlock
        width = 100.pct
    }
    val objectContainer by css {
        width = fitContent
        minWidth = 100.pct
        marginTop = baseSpacing
    }
}

interface ModelViewProps : RProps {
    var model: PositionedModel
}

private interface ModelViewComponentState : RState {
    var containerRef: RReadableRef<HTMLElement>
    var lastModelAdditionTime: Int
    var lastAddedModels: MutableSet<AnyModelObjectIdentity>
    var highlightedObject: AnyModelObjectIdentifier?
}

private interface ModelViewComponentProps : RProps, ModelViewProps {
    var forwardRef: RRef
}

private class ModelViewComponent : RComponent<ModelViewComponentProps, ModelViewComponentState>() {
    private var subscribed = false

    init {
        state = jsObject {
            containerRef = createRef()
            lastModelAdditionTime = 0
            lastAddedModels = HashSet()
            highlightedObject = null
        }
    }

    private val currentTime: Int get() = this.asDynamic().context as Int

    override fun RBuilder.render() {
        ArrowTarget(state.containerRef) {
            ref = props.forwardRef

            styledDiv {
                ref = state.containerRef

                css {
                    +ModelStyles.modelContainer
                    gridColumnStart = GridColumnStart(props.model.position.x.toString())
                    gridRowStart = GridRowStart(props.model.position.y.toString())
                }
                styledH3 {
                    css { +ModelStyles.modelName }
                    +props.model.name
                }
                if (props.model.metamodel.name.isNotBlank()) {
                    styledSpan {
                        css { +ModelStyles.metamodelName }
                        +": "
                        +props.model.metamodel.name
                    }
                }
                styledDiv {
                    css { +ModelStyles.objectContainer }
                    props.model.objects.forEach { modelObject ->
                        val addedTime =
                            if (state.lastAddedModels.any { it.identifies(modelObject) }) state.lastModelAdditionTime
                            else null
                        ModelObjectView(
                            modelObject,
                            addedTime,
                            highlighted = state.highlightedObject?.matches(modelObject) == true,
                            highlighter = ::highlightObject
                        ) {
                            key = modelObject.identity.identifyingString
                        }
                    }
                }
            }
        }
    }

    private val onModelChange = { change: AnyModelObjectChange ->
        if (change is AdditionChange) {
            val currentList = if (state.lastModelAdditionTime < currentTime) HashSet() else state.lastAddedModels
            currentList.add(change.addedObjectIdentity)
            state.containerRef.current?.dispatchEvent(DataChangeEvent())
            setState {
                lastModelAdditionTime = currentTime
                lastAddedModels = currentList
            }
        }
    }

    private fun highlightObject(identifier: AnyModelObjectIdentifier?) {
        if (state.highlightedObject != identifier) setState { highlightedObject = identifier }
    }

    override fun componentDidMount() {
        this.props.subscribe()
    }

    override fun componentWillUnmount() {
        this.props.unsubscribe()
    }

    override fun componentDidUpdate(
        prevProps: ModelViewComponentProps,
        prevState: ModelViewComponentState,
        snapshot: Any
    ) {
        if (this.props.model !== prevProps.model) {
            prevProps.unsubscribe()
            this.props.subscribe()
        }
    }

    private fun ModelViewProps.subscribe() {
        check(!subscribed) { "already subscribed!" }
        model.directChanges.subscribe(onModelChange)
        subscribed = true
    }

    private fun ModelViewProps.unsubscribe() {
        if (subscribed) model.directChanges.unsubscribe(onModelChange)
        else console.log("Not subscribed yet!")
        subscribed = false
    }

    companion object :
        RStatics<ModelViewComponentProps, ModelViewComponentState, ModelViewComponent, RContext<Any>>(ModelViewComponent::class) {
        init {
            contextType = time.unsafeCast<RContext<Any>>()
        }
    }
}

private val modelView = forwardRef<ModelViewProps> { rawProps, forwardRef ->
    val props = rawProps.unsafeCast<ModelViewProps>()

    child(ModelViewComponent::class) {
        attrs.model = props.model
        attrs.forwardRef = forwardRef
    }
}

fun RBuilder.ModelView(model: PositionedModel, handler: RHandler<ModelViewProps>) =
    child(modelView, jsObject<ModelViewProps> {
        this.model = model
    }) {
        handler()
    }
