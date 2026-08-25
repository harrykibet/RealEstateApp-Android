package com.estatia.realestate.apps.core.testing.chaos.contracts

import org.junit.Test

/**
 * Base abstract class for defining "Chaos Contracts" that every adversarial implementation must pass.
 * @param T The type of the implementation under test.
 * @param B The type of behavior/failure used to drive chaos.
 */
abstract class ChaosContract<T, B> {

    abstract fun createSubject(behavior: B): T

    /**
     * Verifies that the subject propagates cancellation correctly.
     */
    @Test
    abstract fun cancellationPropagates()
}
