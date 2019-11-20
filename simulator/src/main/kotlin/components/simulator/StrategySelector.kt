package de.joshuagleitze.transformationnetwork.simulator.components.simulator

import de.joshuagleitze.transformationnetwork.network.PropagationStrategy
import de.joshuagleitze.transformationnetwork.simulator.util.externals.ReactSelect
import de.joshuagleitze.transformationnetwork.simulator.util.externals.ReactSelectEntry
import kotlinext.js.jsObject
import react.RBuilder

fun RBuilder.StrategySelector(
    strategies: List<PropagationStrategy>,
    selectedStrategyIndex: Int,
    setStrategyIndex: (Int) -> Unit
) {
    val options = strategies.mapIndexed { index, strategy ->
        jsObject<ReactSelectEntry<Int>> {
            label = strategy::class.simpleName!!
            value = index
        }
    }
    ReactSelect(
        options,
        value = options[selectedStrategyIndex],
        onChange = { selectedEntry, _ -> if (selectedEntry != null) setStrategyIndex(selectedEntry.value) }
    )
}