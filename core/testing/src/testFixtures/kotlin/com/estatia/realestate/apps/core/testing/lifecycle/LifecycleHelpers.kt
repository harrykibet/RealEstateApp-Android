package com.estatia.realestate.apps.core.testing.lifecycle

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Simulates a standard Android "Launch and Destroy" cycle to test for leaks and coroutine safety.
 */
suspend fun CoroutineScope.launchAndDestroy(
    block: suspend () -> Unit
) {
    val job = launch {
        block()
    }
    // Simulate process death or view destruction
    job.cancel("Simulated destruction")
}
