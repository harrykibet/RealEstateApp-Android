package com.estatia.realestate.apps.core.testing.witness

import org.junit.Assert.assertTrue
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * A lightweight alternative to MockK verification.
 * Fakes can record their interactions as a sequence of [Action]s.
 * 
 * 🏎️ HIGH-PERFORMANCE INTERACTION TRACKING:
 * Uses [ConcurrentLinkedQueue] to ensure that [record] operations are O(1) amortized,
 * even under heavy concurrent load (e.g., thousands of events in chaos stress tests).
 */
class Witness<Action : Any> {
    private val actions = ConcurrentLinkedQueue<Action>()

    fun record(action: Action) {
        actions.add(action)
    }

    fun assertHistory(vararg expected: Action) {
        val actual = actions.toList()
        assertTrue(
            "History mismatch.\nExpected: ${expected.toList()}\nActual: $actual",
            actual == expected.toList()
        )
    }

    fun assertContains(action: Action) {
        assertTrue("Action $action not found in history: $actions", actions.contains(action))
    }

    fun clear() {
        actions.clear()
    }

    fun getActions(): List<Action> {
        return actions.toList()
    }
}
