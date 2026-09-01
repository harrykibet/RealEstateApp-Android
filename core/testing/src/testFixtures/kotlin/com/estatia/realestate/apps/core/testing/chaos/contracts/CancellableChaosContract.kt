package com.estatia.realestate.apps.core.testing.chaos.contracts

import org.junit.Test

/**
 * Extension of [ChaosContract] for implementations that perform asynchronous work
 * and must explicitly honor coroutine cancellation.
 */
abstract class CancellableChaosContract<T, B> : ChaosContract<T, B>() {

    /**
     * Verifies that the subject propagates cancellation correctly.
     *
     * This test MUST be implemented with a real assertion (e.g., using TestScheduler
     * and job.cancel()) to prove that the component doesn't swallow cancellation.
     */
    @Test
    abstract fun cancellationPropagates()
}
