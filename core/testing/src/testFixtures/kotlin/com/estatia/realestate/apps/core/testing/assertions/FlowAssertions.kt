package com.estatia.realestate.apps.core.testing.assertions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Asserts that the flow eventually emits a value matching the predicate.
 */
suspend fun <T> Flow<T>.assertEmits(
    timeout: Duration = 5.seconds,
    predicate: (T) -> Boolean
) {
    withTimeout(timeout) {
        first { predicate(it) }
    }
}

/**
 * Asserts that the first emitted value of the flow is equal to [expected].
 */
suspend fun <T> Flow<T>.assertFirst(expected: T) {
    assertEquals(expected, first())
}
