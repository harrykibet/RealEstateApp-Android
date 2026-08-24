package com.estatia.realestate.apps.core.testing.assertions

import kotlinx.coroutines.flow.StateFlow
import org.junit.Assert.assertTrue

/**
 * Reusable assertions for UI/ViewModel state.
 */
fun <T> StateFlow<T>.assertState(predicate: T.() -> Boolean) {
    assertTrue("State assertion failed. Current state: ${value}", value.predicate())
}

/**
 * Helper to verify that a specific state property matches the expectation.
 */
fun <T, V> StateFlow<T>.assertProperty(expected: V, selector: T.() -> V) {
    val actual = value.selector()
    assertTrue("Expected $expected but got $actual in state $value", actual == expected)
}
