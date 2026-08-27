package com.estatia.realestate.apps.core.testing.witness

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class WitnessTest {

    @Test
    fun `record concurrent calls`() = runBlocking {
        val witness = Witness<Int>()
        val count = 100
        val jobs = List(count) { i ->
            launch(Dispatchers.Default) {
                witness.record(i)
            }
        }
        jobs.joinAll()
        assertEquals(count, witness.getActions().size)
    }

    @Test
    fun `assertHistory success`() {
        val witness = Witness<String>()
        witness.record("A")
        witness.record("B")
        witness.assertHistory("A", "B")
    }

    @Test(expected = AssertionError::class)
    fun `assertHistory failure`() {
        val witness = Witness<String>()
        witness.record("A")
        witness.assertHistory("B")
    }

    @Test
    fun `assertContains success`() {
        val witness = Witness<String>()
        witness.record("A")
        witness.record("B")
        witness.assertContains("A")
        witness.assertContains("B")
    }

    @Test(expected = AssertionError::class)
    fun `assertContains failure`() {
        val witness = Witness<String>()
        witness.record("A")
        witness.assertContains("B")
    }

    @Test
    fun `clear history`() {
        val witness = Witness<String>()
        witness.record("A")
        witness.clear()
        assertEquals(0, witness.getActions().size)
    }
}
