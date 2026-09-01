package com.estatia.realestate.apps.core.testing.chaos.contracts

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import kotlinx.coroutines.CancellationException
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
     * Verifies that the operation fails and maps correctly under chaos.
     *
     * This test MUST be implemented with a real assertion to prove that the component
     * correctly surfaces the chaos behavior (e.g., via exception or AppResult.Error).
     */
    @Test
    abstract fun failureMapsCorrectly()

    /**
     * Verifies that the operation succeeds under normal conditions.
     */
    @Test
    fun successWorks() = runTest {
        val subject = createSubject(successBehavior)
        performOperation(subject)
    }

    /**
     * Helper to verify that an operation either throws an Exception or returns an AppResult.Error
     * when the configured [failureBehavior] is active.
     */
    protected suspend fun assertFailureBehavior() {
        val subject = createSubject(failureBehavior)
        val result = try {
            performOperation(subject)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Success if it throws (e.g. IOException)
            return
        }

        if (result is AppResult.Error) {
            // Success if it returns an error result
            return
        }

        throw AssertionError("Operation should have failed with $failureBehavior")
    }
}
