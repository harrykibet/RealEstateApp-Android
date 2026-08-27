package com.estatia.realestate.apps.core.testing.assertions

import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Test

class StateAssertionsTest {

    data class TestState(val count: Int, val name: String)

    @Test
    fun `assertState success`() {
        val state = MutableStateFlow(TestState(1, "Test"))
        state.assertState { count == 1 && name == "Test" }
    }

    @Test(expected = AssertionError::class)
    fun `assertState failure`() {
        val state = MutableStateFlow(TestState(1, "Test"))
        state.assertState { count == 2 }
    }

    @Test
    fun `assertProperty success`() {
        val state = MutableStateFlow(TestState(1, "Test"))
        state.assertProperty(1) { count }
        state.assertProperty("Test") { name }
    }

    @Test(expected = AssertionError::class)
    fun `assertProperty failure`() {
        val state = MutableStateFlow(TestState(1, "Test"))
        state.assertProperty(2) { count }
    }
}
