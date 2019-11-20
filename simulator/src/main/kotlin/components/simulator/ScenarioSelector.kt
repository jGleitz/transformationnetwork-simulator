package de.joshuagleitze.transformationnetwork.simulator.components.simulator

import de.joshuagleitze.transformationnetwork.simulator.data.scenario.SimulatorScenario
import de.joshuagleitze.transformationnetwork.simulator.util.externals.ReactSelect
import de.joshuagleitze.transformationnetwork.simulator.util.externals.ReactSelectEntry
import kotlinext.js.jsObject
import react.RBuilder

fun RBuilder.ScenarioSelector(
    scenarios: List<SimulatorScenario>,
    selectedScenarioIndex: Int,
    setScenarioIndex: (Int) -> Unit
) {
    val options = scenarios.mapIndexed { index, scenario ->
        jsObject<ReactSelectEntry<Int>> {
            label = scenario.name
            value = index
        }
    }
    ReactSelect(
        options,
        value = options[selectedScenarioIndex],
        onChange = { selectedEntry, _ -> if (selectedEntry != null) setScenarioIndex(selectedEntry.value) }
    )
}