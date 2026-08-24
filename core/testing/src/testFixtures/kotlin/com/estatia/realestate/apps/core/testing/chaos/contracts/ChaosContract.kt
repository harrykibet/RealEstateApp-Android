package com.estatia.realestate.apps.core.testing.chaos.contracts

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import org.junit.Test
import kotlinx.coroutines.test.runTest

/**
 * Base abstract class for defining "Chaos Contracts" that every adversarial implementation must pass.
 */
abstract class ChaosContract<T, B> {

    abstract fun createSubject(behavior: B): T

    /**
     * Verifies that the subject propagates cancellation correctly.
     */
    @Test
    abstract fun cancellationPropagates()
}
