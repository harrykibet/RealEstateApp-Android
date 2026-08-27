package com.estatia.realestate.apps.core.testing.chaos.concurrency

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class ConcurrencyChaosControllerTest {

    private val controller = ConcurrencyChaosController()

    @Test
    fun `ConcurrentMutation injects delay`() = runTest {
        controller.setNextBehavior(ConcurrencyBehavior.ConcurrentMutation)
        val startTime = testScheduler.currentTime
        controller.checkChaos("test")
        val endTime = testScheduler.currentTime
        assertEquals(100L, endTime - startTime)
    }

    @Test
    fun `DuplicateRequest throws on second call`() = runTest {
        controller.setNextBehavior(ConcurrencyBehavior.DuplicateRequest)
        controller.checkChaos("point") // First call OK
        
        try {
            controller.checkChaos("point")
            fail("Should have thrown IllegalStateException")
        } catch (e: IllegalStateException) {
            assertTrue(e.message!!.contains("DuplicateRequest"))
        }
    }

    @Test
    fun `RaceCondition via waitFor and release`() = runTest {
        var resumed = false
        launch {
            controller.waitFor("race")
            resumed = true
        }
        runCurrent()
        
        assertFalse(resumed)
        controller.release("race")
        runCurrent()
        assertTrue(resumed)
    }

    @Test
    fun `reset clears all state`() = runTest {
        controller.setNextBehavior(ConcurrencyBehavior.DuplicateRequest)
        controller.checkChaos("point")
        
        controller.reset()
        
        // Should be Success now, and counts cleared
        controller.checkChaos("point")
        controller.checkChaos("point") // No exception
    }

    @Test
    fun `MultipleRefreshOperations injects delay`() = runTest {
        controller.setNextBehavior(ConcurrencyBehavior.MultipleRefreshOperations)
        val startTime = testScheduler.currentTime
        controller.checkChaos("test")
        val endTime = testScheduler.currentTime
        assertEquals(50L, endTime - startTime)
    }
}
