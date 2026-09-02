package com.estatia.realestate.apps.core.testing.chaos.time

import com.estatia.realestate.apps.core.common.interfaces.IClock
import com.estatia.realestate.apps.core.testing.clock.TestClock
import java.util.concurrent.atomic.AtomicReference

/**
 * Adversarial decorator for [TestClock] that can simulate clock anomalies.
 * 
 * 🏎️ OPERATIONAL FIDELITY:
 * Supports scripting a sequence of behaviors for complex time-anomaly scenarios
 * (e.g., normal flow -> sudden jump -> frozen clock). Consistency with other chaos controllers
 * ensures a predictable mental model for adversarial testing.
 */
class ChaosClock(private val delegate: TestClock) : IClock {

    private data class ScriptState(
        val behaviors: List<TimeBehavior> = emptyList(),
        val index: Int = 0
    )

    private val scriptState = AtomicReference(ScriptState())

    /**
     * Scripts a sequence of behaviors for subsequent [currentTimeMillis] calls.
     */
    fun script(vararg behaviors: TimeBehavior) {
        scriptState.set(ScriptState(behaviors.toList(), 0))
    }

    /**
     * Shortcut for scripting a single next behavior.
     */
    fun setNextBehavior(behavior: TimeBehavior) {
        script(behavior)
    }

    override fun currentTimeMillis(): Long {
        val behavior = popNext()
        val base = delegate.currentTimeMillis()
        return when (behavior) {
            TimeBehavior.ClockSkipForward -> base + 1_000_000L
            TimeBehavior.ClockSkipBackward -> base - 1_000_000L
            TimeBehavior.FrozenClock -> base
            is TimeBehavior.HighJitter -> base + behavior.offsetMillis
            is TimeBehavior.ExtremeDrift -> base + behavior.driftMillis
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

    private fun popNext(): TimeBehavior {
        while (true) {
            val current = scriptState.get()
            val behavior = if (current.index < current.behaviors.size) {
                current.behaviors[current.index]
            } else {
                TimeBehavior.Success
            }

            val next = if (current.index < current.behaviors.size) {
                current.copy(index = current.index + 1)
            } else {
                current
            }

            if (scriptState.compareAndSet(current, next)) {
                return behavior
            }
        }
    }

    /**
     * Clears the current script and resets to Success.
     */
    fun reset() {
        scriptState.set(ScriptState())
    }

    fun advanceBy(millis: Long) = delegate.advanceBy(millis)
    
    fun set(millis: Long) = delegate.set(millis)
}
