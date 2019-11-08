package de.joshuagleitze.transformationnetwork.simulator

import kotlinext.js.jsObject
import org.w3c.dom.get
import org.w3c.dom.set
import react.RBuilder
import react.RComponent
import react.RHandler
import react.RProps
import react.RState
import react.dom.createPortal

private interface AppendDefProps : RProps {
    var id: String
}

private interface AppendDefState : RState {
    var instanceId: String
}

private var idCounter = 0

private const val appendDefIdList = "appendedDefIds"

private class AppendDef : RComponent<AppendDefProps, AppendDefState>() {
    init {
        state = jsObject {
            instanceId = idCounter++.toString()
        }
    }

    override fun RBuilder.render() {
        SvgDefContext.Consumer { defElement ->
            if (defElement != null) {
                val defElementIds = HashMap<String, String>()
                (defElement.dataset[appendDefIdList]?.split(",") ?: emptyList())
                    .associateTo(defElementIds) { it.split("=").let { split -> split[0] to split[1] } }

                if (!defElementIds.containsKey(props.id)) {
                    defElementIds[props.id] = state.instanceId
                    defElement.dataset[appendDefIdList] = (defElementIds + (props.id to state.instanceId)).entries
                        .joinToString(",") { "${it.key}=${it.value}" }
                }

                if (defElementIds[props.id] == state.instanceId) {
                    childList += createPortal(defElement) {
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