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
            TimeBehavior.FrozenClock -> base
            TimeBehavior.HighJitter -> base + (Math.random() * 500).toLong()
            TimeBehavior.ExtremeDrift -> base + (System.currentTimeMillis() % 10000)
            TimeBehavior.Expiration -> base + 100_000_000L
            TimeBehavior.RetryDeadlineExceeded -> base + 60_000L
            TimeBehavior.BoundaryAtExpiration -> base
            TimeBehavior.JustBeforeExpiration -> base - 100L
            TimeBehavior.JustAfterExpiration -> base + 100L
            TimeBehavior.ClockSkew -> base + 5000L
            TimeBehavior.LongRunningOperation -> base + 300_000L
            TimeBehavior.Success -> base
        }
        nextBehavior = TimeBehavior.Success
        return result
    }

    fun advanceBy(millis: Long) = delegate.advanceBy(millis)
}
