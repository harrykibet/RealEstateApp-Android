package com.estatia.realestate.apps.core.testing.coroutine

import kotlinx.coroutines.CompletableDeferred
import java.util.concurrent.ConcurrentHashMap

/**
 * A scheduler for testing concurrent interactions by explicitly pausing execution at named synchronization points.
 * 
 * Example:
 * ```kotlin
 * // In Production/Fake
 * scheduler.awaitPoint("before_save")
 * save()
 * 
 * // In Test
 * launch { subject.doWork() }
 * // ... verify state before save ...
 * scheduler.release("before_save")
 * ```
 */
class TestScheduler {
    private val points = ConcurrentHashMap<String, CompletableDeferred<Unit>>()

    /**
     * Pauses the current coroutine until the specified point is released.
     */
    suspend fun awaitPoint(name: String) {
        getPoint(name).await()
    }

    /**
     * Releases any coroutines waiting at the specified point.
     */
    fun release(name: String) {
        points.remove(name)?.complete(Unit)
    }

    private fun getPoint(name: String): CompletableDeferred<Unit> {
        return points.getOrPut(name) { CompletableDeferred() }
    }

    /**
     * Clears all synchronization points.
     */
    fun clear() {
        points.values.forEach { it.complete(Unit) }
        points.clear()
    }
}
