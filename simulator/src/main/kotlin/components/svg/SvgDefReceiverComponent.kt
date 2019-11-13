package de.joshuagleitze.transformationnetwork.simulator.components.svg

import de.joshuagleitze.transformationnetwork.simulator.util.svg.elementdsl.defs
import kotlinext.js.jsObject
import org.w3c.dom.get
import org.w3c.dom.set
import org.w3c.dom.svg.SVGElement
import react.RBuilder
import react.RComponent
import react.RElementBuilder
import react.RProps
import react.RReadableRef
import react.RState
import react.createContext
import react.createRef
import react.dom.createPortal
import react.setState

val SvgDefContext = createContext<SvgDefReceiver?>(null)

private interface SvgDefReceiverState : RState {
    var defRef: RReadableRef<SVGElement>
    var defElement: SVGElement?
}

private const val defMappings = "appendedDefMappings"

class SvgDefReceiver(private val defElement: SVGElement) {
    fun RBuilder.appendDef(block: RBuilder.() -> Unit) {
        childList += createPortal(defElement) {
            block()
        }
    }

    fun getDefMappings() =
        (defElement.dataset[defMappings]?.nullIfEmpty()?.split(",") ?: emptyList())
            .associate { it.split("=").let { split -> split[0] to split[1] } }


    fun appendDefMapping(key: String, value: String) {
        val currentValue = defElement.dataset[defMappings]?.nullIfEmpty()
        val mappingString = "$key=$value"
        defElement.dataset[defMappings] = if (currentValue == null) mappingString else "$currentValue,$mappingString"
    }

    internal fun clearDefMappings() {
        defElement.dataset[defMappings] = ""
    }

    private fun String.nullIfEmpty() = if (this.isEmpty()) null else this
}

private class SvgDefReceiverComponent : RComponent<RProps, SvgDefReceiverState>() {
    init {
        state = jsObject {
            defRef = createRef()
            defElement = null
        }
    }

    override fun RBuilder.render() {
        defs {
            ref = state.defRef
        }

        val defReceiver = state.defElement?.let { SvgDefReceiver(it) }
        defReceiver?.clearDefMappings()

        SvgDefContext.Provider(defReceiver) {
            props.children()
        }
    }

    override fun componentDidMount() {
        setState { defElement = defRef.current!! }
    }
}

fun RBuilder.SvgDefReceiver(handler: RElementBuilder<*>.() -> Unit) = child(SvgDefReceiverComponent::class, handler)