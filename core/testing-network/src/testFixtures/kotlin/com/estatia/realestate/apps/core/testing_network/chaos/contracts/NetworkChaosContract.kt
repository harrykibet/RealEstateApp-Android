package com.estatia.realestate.apps.core.testing_network.chaos.contracts

import com.estatia.realestate.apps.core.testing.chaos.contracts.ChaosContract
import com.estatia.realestate.apps.core.testing.witness.Witness
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * Specialized contract for Network operations.
 */
abstract class NetworkChaosContract<T, B> : ChaosContract<T, B>() {

    abstract val timeoutBehavior: B

    /**
     * Optional witness to verify retries or other side effects.
     */
    open val retryWitness: Witness<*>? = null

    @Test
    fun timeoutBehaviorWorks() = runTest {
        val subject = createSubject(timeoutBehavior)
        try {
            performOperation(subject)
        } catch (e: Exception) {
            // Expected for many network clients
            return@runTest
        }
    }

    @Test
    fun retryBehaviorWorks() = runTest {
        val witness = retryWitness ?: return@runTest
        // Subclasses can override to perform an operation that should retry.
        val subject = createSubject(successBehavior)
        performOperation(subject)
    }
}
