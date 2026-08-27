package com.estatia.realestate.apps.core.testing.clock

import org.junit.Assert.assertEquals
import org.junit.Test

class TestClockTest {

    @Test
    fun `initial time is respected`() {
        val clock = TestClock(100L)
        assertEquals(100L, clock.currentTimeMillis())
    }

    @Test
    fun `advanceBy increments time`() {
        val clock = TestClock(100L)
        clock.advanceBy(50L)
        assertEquals(150L, clock.currentTimeMillis())
    }

    @Test
    fun `set changes time absolutely`() {
        val clock = TestClock(100L)
        clock.set(500L)
        assertEquals(500L, clock.currentTimeMillis())
    }

    @Test
    fun `currentTimeMillis returns current value`() {
        val clock = TestClock(0L)
        assertEquals(0L, clock.currentTimeMillis())
        clock.advanceBy(10L)
        assertEquals(10L, clock.currentTimeMillis())
    }
}
