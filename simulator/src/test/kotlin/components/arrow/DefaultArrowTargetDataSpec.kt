package de.joshuagleitze.transformationnetwork.simulator.components.arrow

import de.joshuagleitze.transformationnetwork.simulator.assertions.assertRoundedEquals
import de.joshuagleitze.transformationnetwork.simulator.data.arrow.DefaultArrowTargetData
import de.joshuagleitze.transformationnetwork.simulator.util.geometry.Angle
import de.joshuagleitze.transformationnetwork.simulator.util.geometry.Angle.Companion.PI
import de.joshuagleitze.transformationnetwork.simulator.util.geometry.Coordinate
import de.joshuagleitze.transformationnetwork.simulator.util.geometry.times
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultArrowTargetDataSpec {
    @Test
    fun inferredCoordinates() {
        val testTarget = DefaultArrowTargetData(Coordinate(1, 3), Coordinate(2, 1))

        assertEquals(1.0, testTarget.top, "top")
        assertEquals(2.0, testTarget.right, "right")
        assertEquals(3.0, testTarget.bottom, "bottom")
        assertEquals(1.0, testTarget.left, "left")

        assertEquals(Coordinate(1, 1), testTarget.topLeft, "top left")
        assertEquals(Coordinate(2, 3), testTarget.bottomRight, "bottom right")
        assertEquals(Coordinate(1.5, 2), testTarget.center, "center")

        assertEquals(2.0, testTarget.height, "height")
        assertEquals(1.0, testTarget.width, "width")
    }

    @Test
    fun projectToOuterBorder() {
        var testTarget = DefaultArrowTargetData(Coordinate(1, 3), Coordinate(2, 1))

        assertRoundedEquals(Coordinate(2, 2), testTarget.projectToBorder(Angle(0)), "0 * π")
        assertRoundedEquals(Coordinate(1.5, 1), testTarget.projectToBorder(PI / 2), "π / 2")
        assertRoundedEquals(Coordinate(1, 2), testTarget.projectToBorder(PI), "π")
        assertRoundedEquals(Coordinate(1.5, 3), testTarget.projectToBorder(3 * PI / 2), "3 * π / 2")

        assertRoundedEquals(Coordinate(2, 1.5), testTarget.projectToBorder(PI / 4), "π / 4")
        assertRoundedEquals(Coordinate(1, 1.5), testTarget.projectToBorder(3 * PI / 4), "3 * π / 4")
        assertRoundedEquals(Coordinate(1, 2.5), testTarget.projectToBorder(5 * PI / 4), " 5 * π / 4")
        assertRoundedEquals(Coordinate(2, 2.5), testTarget.projectToBorder(7 * PI / 4), "7 * π / 4")


        testTarget = DefaultArrowTargetData(Coordinate(1, 1), Coordinate(2, 1 - sqrt(3.0)))

        assertRoundedEquals(testTarget.topRight, testTarget.projectToBorder(PI / 3), "π / 3")
        assertRoundedEquals(testTarget.topLeft, testTarget.projectToBorder(2 * PI / 3), "2 * π / 3")
        assertRoundedEquals(testTarget.bottomLeft, testTarget.projectToBorder(4 * PI / 3), "4 * π / 3")
        assertRoundedEquals(testTarget.bottomRight, testTarget.projectToBorder(5 * PI / 3), "5 * π / 3")
    }

    @Test
    fun projectToOuterBorderWithDistance() {
        val testTarget = DefaultArrowTargetData(Coordinate(1, 3), Coordinate(2, 1))
        assertRoundedEquals(Coordinate(3, 0.5), testTarget.projectToBorder(PI / 4, sqrt(2.0)), "π / 4")
        assertRoundedEquals(Coordinate(0, 0.5), testTarget.projectToBorder(3 * PI / 4, sqrt(2.0)), "3 * π / 4")
        assertRoundedEquals(Coordinate(0, 3.5), testTarget.projectToBorder(5 * PI / 4, sqrt(2.0)), " 5 * π / 4")
        assertRoundedEquals(Coordinate(3, 3.5), testTarget.projectToBorder(7 * PI / 4, sqrt(2.0)), "7 * π / 4")
    }
}
