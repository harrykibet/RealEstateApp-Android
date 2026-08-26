package com.estatia.realestate.apps.core.testing.chaos.concurrency

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.milliseconds

/**
 * A production-grade concurrency injection harness.
 * Allows tests to define interception points and control execution flow
 * to simulate races, deadlocks, and specific interleavings.
 */
class ConcurrencyChaosController {

    private var nextBehavior: ConcurrencyBehavior = ConcurrencyBehavior.Success
    private val requestCounts = ConcurrentHashMap<String, AtomicInteger>()
    private val barriers = ConcurrentHashMap<String, CompletableDeferred<Unit>>()

    fun setNextBehavior(behavior: ConcurrencyBehavior) {
        nextBehavior = behavior
    }

    /**
     * Injects concurrency chaos based on the configured [ConcurrencyBehavior].
     * @param point A unique identifier for the interception point (e.g., "fetch_user").
     */
    suspend fun checkChaos(point: String) {
        val behavior = nextBehavior
        when (behavior) {
            ConcurrencyBehavior.ConcurrentMutation -> {
                // Widen the race window
                delay(100.milliseconds)
            }
            ConcurrencyBehavior.OutOfOrderResponse -> {
                // Intermittent delay to shuffle response order
                if (Math.random() > 0.5) delay(200.milliseconds)
            }
            ConcurrencyBehavior.CancellationRace -> {
                yield()
            }
            ConcurrencyBehavior.DuplicateRequest,
            ConcurrencyBehavior.DoubleInitialization,
            ConcurrencyBehavior.DoubleRelease,
            ConcurrencyBehavior.OperationAfterDisposal -> {
                val count = requestCounts.getOrPut(point) { AtomicInteger(0) }
                if (count.incrementAndGet() > 1) {
                    throw IllegalStateException("${behavior} detected at $point (Chaos)")
                }
            }
            ConcurrencyBehavior.MultipleRefreshOperations -> {
                 delay(50.milliseconds)
            }
            ConcurrencyBehavior.StaleResult,
            ConcurrencyBehavior.CallbackRace -> {
                // Intermittent long delay to force stale state
                delay(500.milliseconds)
            }
            ConcurrencyBehavior.Success -> Unit
        }
    }

    /**
     * Blocks execution at the given point until [release] is called for that point.
     * Useful for deterministic race condition testing.
     */
    suspend fun waitFor(point: String) {
        getPoint(point).await()
    }

    /**
     * Releases any coroutine waiting at the given point.
     */
    fun release(point: String) {
        getPoint(point).complete(Unit)
    }

    private fun getPoint(name: String): CompletableDeferred<Unit> =
        barriers.computeIfAbsent(name) { CompletableDeferred() }

    /**
     * Clears all state and pending barriers.
     */
    fun reset() {
        nextBehavior = ConcurrencyBehavior.Success
        requestCounts.clear()
        barriers.values.forEach { it.complete(Unit) }
        barriers.clear()
    }
}
