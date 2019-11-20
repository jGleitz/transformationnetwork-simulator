package de.joshuagleitze.transformationnetwork.simulator.style

object Font {
    val defaultFamilies = listOf(
        "-apple-system",
        "BlinkMacSystemFont",
        "Segoe UI",
        "Roboto",
        "Oxygen-Sans",
        "Ubuntu",
        "Cantarell",
        "Helvetica Neue",
        "sans-serif"
    ).joinToString(",") { if (it.contains(' ')) "\"$it\"" else it }
}