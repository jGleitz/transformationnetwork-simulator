package de.joshuagleitze.transformationnetwork.simulator.components.arrow

import de.joshuagleitze.transformationnetwork.simulator.assertions.assertRoundedEquals
import de.joshuagleitze.transformationnetwork.simulator.data.arrow.DefaultArrowTargetData
import de.joshuagleitze.transformationnetwork.simulator.data.arrow.lineBetweenWithMid
import de.joshuagleitze.transformationnetwork.simulator.util.geometry.x
import kotlin.math.sqrt
import kotlin.test.Test

class LineBetweenSpec {
    @Test
    fun lineBetweenWithMid() {
        val start = DefaultArrowTargetData((1 x 5.5), (2 x 3.5))
        val end = DefaultArrowTargetData((3.5 x 2), (5.5 x 1))

        val line = lineBetweenWithMid(start, end)
        assertRoundedEquals((2 x 4), line.start, "start")
        assertRoundedEquals((3 x 3), line.coordinates[1], "mid")
        assertRoundedEquals((4 x 2), line.end, "end")
        assertRoundedEquals(sqrt(8.0), line.length, "length")
    }

    @Test
    fun lineBetweenWithMidReversed() {
        val end = DefaultArrowTargetData((1 x 5.5), (2 x 3.5))
        val start = DefaultArrowTargetData((3.5 x 2), (5.5 x 1))

        val line = lineBetweenWithMid(start, end)
        assertRoundedEquals((2 x 4), line.end, "end")
        assertRoundedEquals((3 x 3), line.coordinates[1], "mid")
        assertRoundedEquals((4 x 2), line.start, "start")
        assertRoundedEquals(sqrt(8.0), line.length, "length")
    }

    @Test
    fun lineBetweenWithSpacing() {
        val start = DefaultArrowTargetData((1 x 5.5), (2 x 3.5))
        val end = DefaultArrowTargetData((3.5 x 2), (5.5 x 1))

        val line = lineBetweenWithMid(start, end, sqrt(0.5))
        assertRoundedEquals((2.5 x 3.5), line.start, "start")
        assertRoundedEquals((3 x 3), line.coordinates[1], "mid")
        assertRoundedEquals((3.5 x 2.5), line.end, "end")
        assertRoundedEquals(sqrt(2.0), line.length, "length")
    }

    @Test
    fun lineBetweenWithSpacingReversed() {
        val end = DefaultArrowTargetData((1 x 5.5), (2 x 3.5))
        val start = DefaultArrowTargetData((3.5 x 2), (5.5 x 1))

        val line = lineBetweenWithMid(start, end, sqrt(0.5))
        assertRoundedEquals((2.5 x 3.5), line.end, "end")
        assertRoundedEquals((3 x 3), line.coordinates[1], "mid")
        assertRoundedEquals((3.5 x 2.5), line.start, "start")
        assertRoundedEquals(sqrt(2.0), line.length, "length")
    }
}
