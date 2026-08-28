package com.estatia.realestate.apps.core.testing.clock

import com.estatia.realestate.apps.core.common.interfaces.IClock
import java.util.concurrent.atomic.AtomicLong

/**
 * A deterministic clock for testing that allows manual advancement of time.
 */
class TestClock(initialTime: Long = 0L) : IClock {
    private val time = AtomicLong(initialTime)

    /**
     * Returns the current time in milliseconds.
     */
    override fun currentTimeMillis(): Long = time.get()

    /**
     * Advances the clock by the specified number of milliseconds.
     */
    fun advanceBy(millis: Long) {
        time.addAndGet(millis)
    }

    /**
     * Sets the clock to a specific time.
     */
    fun set(millis: Long) {
        time.set(millis)
    }
}
