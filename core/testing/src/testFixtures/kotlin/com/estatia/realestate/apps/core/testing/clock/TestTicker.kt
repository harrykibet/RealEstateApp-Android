package com.estatia.realestate.apps.core.testing.clock

/**
 * A high-frequency ticker for simulating micro-steps in time, 
 * useful for testing animations, streaming buffers, or polling logic.
 */
class TestTicker(private val clock: TestClock) {
    /**
     * Advances the clock by a fixed interval [count] times.
     */
    fun tick(count: Int, intervalMillis: Long = 16L) {
        repeat(count) {
            clock.advanceBy(intervalMillis)
        }
    }
}
