package de.joshuagleitze.transformationnetwork.simulator.components.arrow

import de.joshuagleitze.transformationnetwork.simulator.assertions.assertRoundedEquals
import de.joshuagleitze.transformationnetwork.simulator.data.arrow.DefaultArrowTargetData
import de.joshuagleitze.transformationnetwork.simulator.data.arrow.lineBetweenWithMid
import de.joshuagleitze.transformationnetwork.simulator.util.geometry.Coordinate
import kotlin.math.sqrt
import kotlin.test.Test

class LineBetweenSpec {
    @Test
    fun lineBetweenWithMid() {
        val start = DefaultArrowTargetData(Coordinate(1, 5.5), Coordinate(2, 3.5))
        val end = DefaultArrowTargetData(Coordinate(3.5, 2), Coordinate(5.5, 1))

        val line = lineBetweenWithMid(start, end)
        assertRoundedEquals(Coordinate(2, 4), line.start, "start")
        assertRoundedEquals(Coordinate(3, 3), line.coordinates[1], "mid")
        assertRoundedEquals(Coordinate(4, 2), line.end, "end")
        assertRoundedEquals(sqrt(8.0), line.length, "length")
    }

    @Test
    fun lineBetweenWithMidReversed() {
        val end = DefaultArrowTargetData(Coordinate(1, 5.5), Coordinate(2, 3.5))
        val start = DefaultArrowTargetData(Coordinate(3.5, 2), Coordinate(5.5, 1))

        val line = lineBetweenWithMid(start, end)
        assertRoundedEquals(Coordinate(2, 4), line.end, "end")
        assertRoundedEquals(Coordinate(3, 3), line.coordinates[1], "mid")
        assertRoundedEquals(Coordinate(4, 2), line.start, "start")
        assertRoundedEquals(sqrt(8.0), line.length, "length")
    }

    @Test
    fun lineBetweenWithSpacing() {
        val start = DefaultArrowTargetData(Coordinate(1, 5.5), Coordinate(2, 3.5))
        val end = DefaultArrowTargetData(Coordinate(3.5, 2), Coordinate(5.5, 1))

        val line = lineBetweenWithMid(start, end, sqrt(0.5))
        assertRoundedEquals(Coordinate(2.5, 3.5), line.start, "start")
        assertRoundedEquals(Coordinate(3, 3), line.coordinates[1], "mid")
        assertRoundedEquals(Coordinate(3.5, 2.5), line.end, "end")
        assertRoundedEquals(sqrt(2.0), line.length, "length")
    }

    @Test
    fun lineBetweenWithSpacingReversed() {
        val end = DefaultArrowTargetData(Coordinate(1, 5.5), Coordinate(2, 3.5))
        val start = DefaultArrowTargetData(Coordinate(3.5, 2), Coordinate(5.5, 1))

        val line = lineBetweenWithMid(start, end, sqrt(0.5))
        assertRoundedEquals(Coordinate(2.5, 3.5), line.end, "end")
        assertRoundedEquals(Coordinate(3, 3), line.coordinates[1], "mid")
        assertRoundedEquals(Coordinate(3.5, 2.5), line.start, "start")
        assertRoundedEquals(sqrt(2.0), line.length, "length")
    }
}
