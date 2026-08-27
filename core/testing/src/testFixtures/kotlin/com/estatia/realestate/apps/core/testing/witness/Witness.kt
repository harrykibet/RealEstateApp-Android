package com.estatia.realestate.apps.core.testing.witness

import org.junit.Assert.assertTrue
import java.util.concurrent.CopyOnWriteArrayList

/**
 * A lightweight alternative to MockK verification.
 * Fakes can record their interactions as a list of [Action]s.
 */
class Witness<Action> {
    private val actions = CopyOnWriteArrayList<Action>()

    fun record(action: Action) {
        actions.add(action)
    }

    fun assertHistory(vararg expected: Action) {
        assertTrue(
            "History mismatch.\nExpected: ${expected.toList()}\nActual: $actions",
            actions == expected.toList()
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
