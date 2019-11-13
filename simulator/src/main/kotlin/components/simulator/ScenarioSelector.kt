package de.joshuagleitze.transformationnetwork.simulator.components.simulator

import de.joshuagleitze.transformationnetwork.simulator.data.scenario.SimulatorScenario
import de.joshuagleitze.transformationnetwork.simulator.styles.Dimension.baseSpacing
import de.joshuagleitze.transformationnetwork.simulator.util.externals.ReactSelect
import de.joshuagleitze.transformationnetwork.simulator.util.externals.ReactSelectEntry
import kotlinext.js.jsObject
import kotlinx.css.Align.center
import kotlinx.css.BoxSizing.borderBox
import kotlinx.css.Display.grid
import kotlinx.css.GridColumnStart
import kotlinx.css.GridTemplateColumns
import kotlinx.css.alignSelf
import kotlinx.css.boxSizing
import kotlinx.css.display
import kotlinx.css.gridColumnStart
import kotlinx.css.gridTemplateColumns
import kotlinx.css.padding
import kotlinx.css.paddingRight
import kotlinx.css.pct
import kotlinx.css.width
import react.RBuilder
import styled.StyleSheet
import styled.css
import styled.styledDiv

private object ScenarioSelectorStyles : StyleSheet("ScenarioSelector") {
    val container by css {
        display = grid
        width = 100.pct
        gridTemplateColumns = GridTemplateColumns.repeat("1, [label] auto [selector] 1fr")
        padding(vertical = baseSpacing, horizontal = baseSpacing * 2)
        boxSizing = borderBox
    }
    val label by css {
        gridColumnStart = GridColumnStart("label")
        alignSelf = center
        paddingRight = baseSpacing
    }
    val selector by css {
        gridColumnStart = GridColumnStart("selector")
    }
}

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
    styledDiv {
        css { +ScenarioSelectorStyles.container }
        styledDiv {
            css { +ScenarioSelectorStyles.label }
            +"Scenario: "
        }
        styledDiv {
            css { +ScenarioSelectorStyles.selector }
            ReactSelect(
                options,
                value = options[selectedScenarioIndex],
                onChange = { selectedEntry, _ -> if (selectedEntry != null) setScenarioIndex(selectedEntry.value) }
            )
        }
    }
}