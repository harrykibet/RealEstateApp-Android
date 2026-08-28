package com.estatia.realestate.apps.core.common.interfaces

/**
 * Interface for time-related operations to allow for deterministic testing.
 */
interface IClock {
    /**
     * Returns the current time in milliseconds.
     */
    fun currentTimeMillis(): Long
}

/**
 * Production implementation of [IClock] using system time.
 */
class SystemClock : IClock {
    override fun currentTimeMillis(): Long = System.currentTimeMillis()
}
