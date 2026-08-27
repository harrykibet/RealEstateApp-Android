package com.estatia.realestate.apps.core.testing.chaos.time

import com.estatia.realestate.apps.core.testing.clock.TestClock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChaosClockTest {

    private val baseClock = TestClock(1000L)
    private val chaosClock = ChaosClock(baseClock)

    @Test
    fun `resets to Success after script is exhausted`() {
        chaosClock.setNextBehavior(TimeBehavior.ClockSkipForward)
        val firstCall = chaosClock.currentTimeMillis()
        val secondCall = chaosClock.currentTimeMillis()
        
        assertEquals(1000L + 1_000_000L, firstCall)
        assertEquals(1000L, secondCall)
    }

    @Test
    fun `honors script sequence across multiple calls`() {
        chaosClock.script(
            TimeBehavior.Success,
            TimeBehavior.ClockSkipForward,
            TimeBehavior.ClockSkipBackward,
            TimeBehavior.Success
        )

        assertEquals(1000L, chaosClock.currentTimeMillis())               // Success
        assertEquals(1000L + 1_000_000L, chaosClock.currentTimeMillis())  // Forward
        assertEquals(1000L - 1_000_000L, chaosClock.currentTimeMillis())  // Backward
        assertEquals(1000L, chaosClock.currentTimeMillis())               // Success
        assertEquals(1000L, chaosClock.currentTimeMillis())               // Exhausted -> Success
    }

    @Test
    fun `reset clears pending script`() {
        chaosClock.script(TimeBehavior.ClockSkipForward)
        chaosClock.reset()
        assertEquals(1000L, chaosClock.currentTimeMillis())
    }

    @Test
    fun `ClockSkipForward adds large offset`() {
        chaosClock.setNextBehavior(TimeBehavior.ClockSkipForward)
        assertEquals(1000L + 1_000_000L, chaosClock.currentTimeMillis())
    }

    @Test
    fun `ClockSkipBackward subtracts large offset`() {
        chaosClock.setNextBehavior(TimeBehavior.ClockSkipBackward)
        assertEquals(1000L - 1_000_000L, chaosClock.currentTimeMillis())
    }

    @Test
    fun `FrozenClock returns base time`() {
        chaosClock.setNextBehavior(TimeBehavior.FrozenClock)
        assertEquals(1000L, chaosClock.currentTimeMillis())
    }

    @Test
    fun `HighJitter adds random noise`() {
        chaosClock.setNextBehavior(TimeBehavior.HighJitter)
        val time = chaosClock.currentTimeMillis()
        assertTrue(time in 1000L..1500L)
    }

    @Test
    fun `ExtremeDrift adds drift`() {
        chaosClock.setNextBehavior(TimeBehavior.ExtremeDrift)
        val time = chaosClock.currentTimeMillis()
        // Drift is based on System.currentTimeMillis() % 10000 in ChaosClock implementation
        assertTrue(time >= 1000L)
    }

    @Test
    fun `Expiration adds large expiration offset`() {
        chaosClock.setNextBehavior(TimeBehavior.Expiration)
        assertEquals(1000L + 100_000_000L, chaosClock.currentTimeMillis())
    }

    @Test
    fun `RetryDeadlineExceeded adds deadline offset`() {
        chaosClock.setNextBehavior(TimeBehavior.RetryDeadlineExceeded)
        assertEquals(1000L + 60_000L, chaosClock.currentTimeMillis())
    }

    @Test
    fun `BoundaryAtExpiration returns base`() {
        chaosClock.setNextBehavior(TimeBehavior.BoundaryAtExpiration)
        assertEquals(1000L, chaosClock.currentTimeMillis())
    }

    @Test
    fun `JustBeforeExpiration subtracts small offset`() {
        chaosClock.setNextBehavior(TimeBehavior.JustBeforeExpiration)
        assertEquals(1000L - 100L, chaosClock.currentTimeMillis())
    }

    @Test
    fun `JustAfterExpiration adds small offset`() {
        chaosClock.setNextBehavior(TimeBehavior.JustAfterExpiration)
        assertEquals(1000L + 100L, chaosClock.currentTimeMillis())
    }

    @Test
    fun `ClockSkew adds skew`() {
        chaosClock.setNextBehavior(TimeBehavior.ClockSkew)
        assertEquals(1000L + 5000L, chaosClock.currentTimeMillis())
    }

    @Test
    fun `LongRunningOperation adds large operation delay`() {
        chaosClock.setNextBehavior(TimeBehavior.LongRunningOperation)
        assertEquals(1000L + 300_000L, chaosClock.currentTimeMillis())
    }

    @Test
    fun `Success returns base time`() {
        chaosClock.setNextBehavior(TimeBehavior.Success)
        assertEquals(1000L, chaosClock.currentTimeMillis())
    }
}
