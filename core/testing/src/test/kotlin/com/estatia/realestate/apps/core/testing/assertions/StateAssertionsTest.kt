package com.estatia.realestate.apps.core.testing.assertions

import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Test

class StateAssertionsTest {

    data class TestState(val count: Int, val name: String)

    @Test
    fun `assertCurrentState success`() {
        val state = MutableStateFlow(TestState(1, "Test"))
        state.assertCurrentState { count == 1 && name == "Test" }
    }

    @Test(expected = AssertionError::class)
    fun `assertCurrentState failure`() {
        val state = MutableStateFlow(TestState(1, "Test"))
        state.assertCurrentState { count == 2 }
    }

    @Test
    fun `assertCurrentProperty success`() {
        val state = MutableStateFlow(TestState(1, "Test"))
        state.assertCurrentProperty(1) { count }
        state.assertCurrentProperty("Test") { name }
    }

    @Test(expected = AssertionError::class)
    fun `assertCurrentProperty failure`() {
        val state = MutableStateFlow(TestState(1, "Test"))
        state.assertCurrentProperty(2) { count }
    }
}
