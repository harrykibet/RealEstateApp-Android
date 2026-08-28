package com.estatia.realestate.apps.core.testing.chaos.time

import com.estatia.realestate.apps.core.common.interfaces.IClock
import com.estatia.realestate.apps.core.testing.clock.TestClock

/**
 * Adversarial decorator for [TestClock] that can simulate clock anomalies.
 * 
 * 🏎️ OPERATIONAL FIDELITY:
 * Supports scripting a sequence of behaviors for complex time-anomaly scenarios
 * (e.g., normal flow -> sudden jump -> frozen clock). Consistency with other chaos controllers
 * ensures a predictable mental model for adversarial testing.
 */
class ChaosClock(private val delegate: TestClock) : IClock {

    private var script: List<TimeBehavior> = emptyList()
    private var currentIndex = 0

    /**
     * Scripts a sequence of behaviors for subsequent [currentTimeMillis] calls.
     */
    fun script(vararg behaviors: TimeBehavior) {
        script = behaviors.toList()
        currentIndex = 0
    }

    /**
     * Shortcut for scripting a single next behavior.
     */
    fun setNextBehavior(behavior: TimeBehavior) {
        script(behavior)
    }

    override fun currentTimeMillis(): Long {
        val behavior = if (currentIndex < script.size) {
            script[currentIndex++]
        } else {
            TimeBehavior.Success
        }

        val base = delegate.currentTimeMillis()
        return when (behavior) {
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
    }

    /**
     * Clears the current script and resets to Success.
     */
    fun reset() {
        script = emptyList()
        currentIndex = 0
    }

    fun advanceBy(millis: Long) = delegate.advanceBy(millis)
    
    fun set(millis: Long) = delegate.set(millis)
}
