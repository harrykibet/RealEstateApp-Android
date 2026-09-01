package com.estatia.realestate.apps.core.testing.assertions

import kotlinx.coroutines.flow.StateFlow
import org.junit.Assert.assertTrue

/**
 * Reusable assertions for UI/ViewModel state.
 *
 * NOTE: This is a synchronous snapshot assertion. It only checks the [StateFlow.value]
 * at the instant it is called. It does NOT wait for emissions or synchronize with
 * asynchronous work. Use Turbine or Flow.first() if you need to wait for a state transition.
 */
fun <T> StateFlow<T>.assertCurrentState(predicate: T.() -> Boolean) {
    assertTrue("State assertion failed. Current state: ${value}", value.predicate())
}

/**
 * Helper to verify that a specific state property matches the expectation in the
 * current snapshot of the [StateFlow].
 */
fun <T, V> StateFlow<T>.assertCurrentProperty(expected: V, selector: T.() -> V) {
    val actual = value.selector()
    assertTrue("Expected $expected but got $actual in state $value", actual == expected)
}
