package com.estatia.realestate.apps.core.testing.coroutine

import kotlinx.coroutines.CompletableDeferred
import java.util.concurrent.ConcurrentHashMap

/**
 * A scheduler for testing concurrent interactions by explicitly pausing execution at named synchronization points.
 * 
 * Example:
 * ```kotlin
 * // In Production/Fake
 * scheduler.release("reached_api_call")
 * api.call()
 * 
 * // In Test
 * launchAndDestroy(scheduler, "reached_api_call") {
 *     subject.doWork()
 * }
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
     * Releases any coroutines waiting at the specified point, or ensures that any 
     * future call to [awaitPoint] with this name resumes immediately.
     */
    fun release(name: String) {
        getPoint(name).complete(Unit)
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
