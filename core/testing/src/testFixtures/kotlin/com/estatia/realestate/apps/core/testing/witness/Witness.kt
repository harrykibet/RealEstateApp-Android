package com.estatia.realestate.apps.core.testing.witness

import org.junit.Assert.assertTrue

/**
 * A lightweight alternative to MockK verification.
 * Fakes can record their interactions as a list of [Action]s.
 */
class Witness<Action> {
    private val actions = mutableListOf<Action>()

    fun record(action: Action) {
        synchronized(actions) {
            actions.add(action)
        }
    }

    fun assertHistory(vararg expected: Action) {
        synchronized(actions) {
            assertTrue(
                "History mismatch.\nExpected: ${expected.toList()}\nActual: $actions",
                actions == expected.toList()
            )
        }
    }

    fun assertContains(action: Action) {
        synchronized(actions) {
            assertTrue("Action $action not found in history: $actions", actions.contains(action))
        }
    }

    fun clear() {
        synchronized(actions) {
            actions.clear()
        }
    }

    fun getActions(): List<Action> {
        return synchronized(actions) {
            actions.toList()
        }
    }
}
