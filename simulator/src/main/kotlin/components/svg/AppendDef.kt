package de.joshuagleitze.transformationnetwork.simulator.components.svg

import kotlinext.js.jsObject
import react.RBuilder
import react.RComponent
import react.RHandler
import react.RProps
import react.RState

private interface AppendDefProps : RProps {
    var id: String
}

private interface AppendDefState : RState {
    var instanceId: String
}

private var idCounter = 0

private class AppendDef : RComponent<AppendDefProps, AppendDefState>() {
    init {
        state = jsObject {
            instanceId = idCounter++.toString()
        }
    }

    override fun RBuilder.render() {
        SvgDefContext.Consumer { defReceiver ->
            if (defReceiver == null) return@Consumer

            val defElementIds = defReceiver.getDefMappings()

            if (!defElementIds.containsKey(props.id)) {
                defReceiver.appendDefMapping(props.id, state.instanceId)
            }

            if ((defElementIds[props.id] ?: state.instanceId) == state.instanceId) {
                with(defReceiver) {
                    appendDef {
                        props.children()
                    }
                }
            }
        }
    }
}

fun RBuilder.AppendDef(id: String, handler: RHandler<*>) = child(AppendDef::class) {
    attrs.id = id
    handler()
}