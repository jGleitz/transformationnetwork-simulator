package de.joshuagleitze.transformationnetwork.simulator.util.geometry

import de.joshuagleitze.transformationnetwork.simulator.assertions.assertEqualsWithComparable
import de.joshuagleitze.transformationnetwork.simulator.assertions.assertFirstLess
import kotlin.math.PI
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class AngleSpec {
    @Test
    fun equality() {
        assertEqualsWithComparable(Angle.PI, Angle(PI))
        assertEqualsWithComparable(Angle(.0), Angle.PI * -2)
        assertEqualsWithComparable(Angle(.0), Angle.PI * 2)
        assertEqualsWithComparable(Angle(.0), Angle.PI * 4)
        assertEqualsWithComparable(Angle(.0), Angle.PI * 8)

        assertEqualsWithComparable(Angle.PI, -Angle.PI)
    }

    @Test
    fun comparing() {
        assertFirstLess(Angle(.0), Angle.PI)
        assertFirstLess(Angle.PI * 2, Angle.PI)
    }

    @Test
    fun between() {
        assertEquals(Angle(0), Angle.between(Coordinate(1, 1), Coordinate(3, 1)))
        assertEquals(Angle.PI / 4, Angle.between(Coordinate(1, 3), Coordinate(3, 1)))

        assertEquals(-Angle.PI, Angle.between(Coordinate(3, 1), Coordinate(1, 1)))
        assertEquals(-Angle.PI / 3, Angle.between(Coordinate(0, 0), Coordinate(1, sqrt(3.0))))
    }
}