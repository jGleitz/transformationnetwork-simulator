@file:JsModule("react-select")

package de.joshuagleitze.transformationnetwork.simulator.util.externals

import react.Component
import react.RState
import react.ReactElement

@JsName("default")
external class ReactSelect<T> : Component<ReactSelectProps<T>, RState> {
    override fun render(): ReactElement?
}