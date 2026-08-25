package com.estatia.realestate.apps.core.testing.lifecycle

import com.estatia.realestate.apps.core.testing.coroutine.TestScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

/**
 * Simulates a standard Android "Launch and Destroy" cycle to test for leaks and coroutine safety.
 *
 * @param scheduler Optional [TestScheduler] for precise mid-flight cancellation.
 * @param atPoint The named synchronization point where the cancellation should occur.
 * @param block The coroutine block to execute.
 */
suspend fun CoroutineScope.launchAndDestroy(
    scheduler: TestScheduler? = null,
    atPoint: String? = null,
    block: suspend () -> Unit
) {
    val job = launch {
        block()
    }

    if (scheduler != null && atPoint != null) {
        // 🧪 Mid-Flight Cancellation:
        // Wait until the block signals it has reached the targeted point
        scheduler.awaitPoint(atPoint)
    } else {
        // 🏁 Immediate Cancellation:
        // Yield to give the coroutine a chance to start, but cancel nearly immediately.
        yield()
    }

    job.cancel("Simulated destruction")
    job.join()
}
