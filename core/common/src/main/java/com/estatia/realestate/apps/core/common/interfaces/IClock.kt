package com.estatia.realestate.apps.core.common.interfaces

import com.estatia.realestate.apps.core.architecture.annotations.Helper
import com.estatia.realestate.apps.core.architecture.annotations.Contract

/**
 * Interface for time-related operations to allow for deterministic testing.
 */
@Contract
fun interface IClock {
    /**
     * Returns the current time in milliseconds.
     */
    fun currentTimeMillis(): Long
}

/**
 * Production implementation of [IClock] using system time.
 */
@Helper
class SystemClock : IClock {
    override fun currentTimeMillis(): Long = System.currentTimeMillis()
}
