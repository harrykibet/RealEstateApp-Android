package com.estatia.realestate.apps.core.testing.chaos.concurrency

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration.Companion.milliseconds

/**
 * A production-grade concurrency injection harness.
 * Allows tests to define interception points and control execution flow
 * to simulate races, deadlocks, and specific interleavings.
 */
class ConcurrencyChaosController {

    private val nextBehavior = AtomicReference<ConcurrencyBehavior>(ConcurrencyBehavior.Success)
    private val requestCounts = ConcurrentHashMap<String, AtomicInteger>()
    private val barriers = ConcurrentHashMap<String, CompletableDeferred<Unit>>()

    fun setNextBehavior(behavior: ConcurrencyBehavior) {
        nextBehavior.set(behavior)
    }

    /**
     * Injects concurrency chaos based on the configured [ConcurrencyBehavior].
     * @param point A unique identifier for the interception point (e.g., "fetch_user").
     */
    suspend fun checkChaos(point: String) {
        val behavior = nextBehavior.get()
        when (behavior) {
            is ConcurrencyBehavior.ConcurrentMutation -> {
                // Widen the race window
                delay(behavior.delayMillis.milliseconds)
            }
            is ConcurrencyBehavior.OutOfOrderResponse -> {
                // Deterministic delay to shuffle response order
                delay(behavior.delayMillis.milliseconds)
            }
            is ConcurrencyBehavior.RandomInterleaving -> {
                // Probabilistic stress: non-deterministic delay
                if (Math.random() < behavior.probability) {
                    delay(behavior.delayMillis.milliseconds)
                }
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
                    throw IllegalStateException("${behavior::class.simpleName} detected at $point (Chaos)")
                }
            }
            is ConcurrencyBehavior.MultipleRefreshOperations -> {
                 delay(behavior.delayMillis.milliseconds)
            }
            is ConcurrencyBehavior.StaleResult -> {
                delay(behavior.delayMillis.milliseconds)
            }
            ConcurrencyBehavior.CallbackRace -> {
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
        nextBehavior.set(ConcurrencyBehavior.Success)
        requestCounts.clear()
        barriers.values.forEach { it.complete(Unit) }
        barriers.clear()
    }
}
