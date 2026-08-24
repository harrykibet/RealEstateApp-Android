package com.estatia.realestate.apps.core.testing.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * Runs multiple blocks concurrently and waits for all of them to complete.
 * Used for testing race conditions.
 */
suspend fun runConcurrent(
    vararg blocks: suspend CoroutineScope.() -> Unit
) = coroutineScope {
    blocks.map { async { it() } }.awaitAll()
}

/**
 * Runs two blocks concurrently with a synchronization point.
 */
suspend fun runConcurrent(
    first: suspend () -> Unit,
    second: suspend () -> Unit,
    scheduler: TestScheduler,
    synchronizationPoint: String
) = coroutineScope {
    val job1 = async { 
        first()
    }
    val job2 = async {
        scheduler.awaitPoint(synchronizationPoint)
        second()
    }
    
    awaitAll(job1, job2)
}
