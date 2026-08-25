package com.estatia.realestate.apps.core.testing.chaos.time

import com.estatia.realestate.apps.core.testing.clock.TestClock

/**
 * Adversarial decorator for [TestClock] that can simulate clock anomalies.
 */
class ChaosClock(private val delegate: TestClock) {

    private var nextBehavior: TimeBehavior = TimeBehavior.Success

    fun setNextBehavior(behavior: TimeBehavior) {
        nextBehavior = behavior
    }

    fun currentTimeMillis(): Long {
        val base = delegate.currentTimeMillis()
        val result = when (nextBehavior) {
            TimeBehavior.ClockSkipForward -> base + 1_000_000L
            TimeBehavior.ClockSkipBackward -> base - 1_000_000L
            TimeBehavior.FrozenClock -> base // Assuming delegate is advanced externally
            TimeBehavior.ClockSkew -> base + 5000L
            TimeBehavior.Success -> base
            else -> base
        }
        nextBehavior = TimeBehavior.Success
        return result
    }

    fun advanceBy(millis: Long) = delegate.advanceBy(millis)
}
