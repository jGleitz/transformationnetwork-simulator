package de.joshuagleitze.transformationnetwork.simulator.util.externals

import react.RBuilder
import react.RProps
import kotlin.reflect.KClass

interface ReactSelectEntry<T> {
    var value: T
    var label: String
}

interface ReactSelectProps<T> : RProps {
    var options: Array<out ReactSelectEntry<T>>
    var value: ReactSelectEntry<T>?
    var onChange: (selectedEntry: ReactSelectEntry<T>?, action: String) -> Unit
}

@Suppress("UNCHECKED_CAST")
fun <T> RBuilder.ReactSelect(
    options: List<ReactSelectEntry<T>>,
    value: ReactSelectEntry<T>? = null,
    onChange: (selectedEntry: ReactSelectEntry<T>?, action: String) -> Unit = { _, _ -> }
) = child(ReactSelect::class as KClass<ReactSelect<T>>) {
    attrs.options = options.toTypedArray()
    attrs.value = value
    attrs.onChange = onChange
}
