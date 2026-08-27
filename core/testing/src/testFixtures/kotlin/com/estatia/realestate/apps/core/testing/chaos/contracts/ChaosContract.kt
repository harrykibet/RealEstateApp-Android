package com.estatia.realestate.apps.core.testing.chaos.contracts

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * Base abstract class for defining "Chaos Contracts" that every adversarial implementation must pass.
 * @param T The type of the implementation under test.
 * @param B The type of behavior/failure used to drive chaos.
 */
abstract class ChaosContract<T, B> {

    abstract val successBehavior: B
    abstract val failureBehavior: B

    abstract fun createSubject(behavior: B): T

    abstract suspend fun performOperation(subject: T): Any?

    /**
     * Verifies that the subject propagates cancellation correctly.
     */
    @Test
    abstract fun cancellationPropagates()

    /**
     * Verifies that the operation succeeds under normal conditions.
     */
    @Test
    fun successWorks() = runTest {
        val subject = createSubject(successBehavior)
        performOperation(subject)
    }

    /**
     * Verifies that the operation fails and maps correctly under chaos.
     */
    @Test
    open fun failureMapsCorrectly() = runTest {
        val subject = createSubject(failureBehavior)
        val result = try {
            performOperation(subject)
        } catch (e: Exception) {
            // Success if it throws
            return@runTest
        }

        if (result is AppResult.Error) {
            // Success if it returns error
            return@runTest
        }

        throw AssertionError("Operation should have failed with $failureBehavior")
    }
}
