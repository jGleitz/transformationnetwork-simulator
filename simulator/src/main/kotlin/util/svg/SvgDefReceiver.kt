package de.joshuagleitze.transformationnetwork.simulator

import de.joshuagleitze.transformationnetwork.simulator.svgdsl.defs
import kotlinext.js.jsObject
import org.w3c.dom.HTMLElement
import react.RBuilder
import react.RComponent
import react.RElementBuilder
import react.RProps
import react.RReadableRef
import react.RState
import react.createContext
import react.createRef
import react.setState

val SvgDefContext = createContext<HTMLElement?>(null)

private interface SvgDefReceiverState : RState {
    var defRef: RReadableRef<HTMLElement>
    var defElement: HTMLElement?
}

private class SvgDefReceiver : RComponent<RProps, SvgDefReceiverState>() {
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
        SvgDefContext.Provider(state.defElement) {
            props.children()
        }
    }

    override fun componentDidMount() {
        setState { defElement = defRef.current!! }
    }
}

fun RBuilder.SvgDefReceiver(handler: RElementBuilder<*>.() -> Unit) = child(SvgDefReceiver::class, handler)