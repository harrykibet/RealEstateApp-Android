package com.estatia.realestate.apps.core.testing.coroutine

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TestSchedulerTest {

    @Test
    fun `awaitPoint blocks until released`() = runTest {
        val scheduler = TestScheduler()
        var resumed = false
        
        launch {
            scheduler.awaitPoint("point")
            resumed = true
        }
        runCurrent()
        
        assertFalse(resumed)
        scheduler.release("point")
        runCurrent()
        assertTrue(resumed)
    }

    @Test
    fun `release before awaitPoint resumes immediately`() = runTest {
        val scheduler = TestScheduler()
        scheduler.release("point")
        
        var resumed = false
        launch {
            scheduler.awaitPoint("point")
            resumed = true
        }
        runCurrent()
        
        assertTrue(resumed)
    }

    @Test
    fun `multiple waiters at same point`() = runTest {
        val scheduler = TestScheduler()
        var resumedCount = 0
        
        repeat(3) {
            launch {
                scheduler.awaitPoint("point")
                resumedCount++
            }
        }
        runCurrent()
        
        assertEquals(0, resumedCount)
        scheduler.release("point")
        runCurrent()
        assertEquals(3, resumedCount)
    }

    @Test
    fun `clear releases all and resets`() = runTest {
        val scheduler = TestScheduler()
        var resumed = false
        
        launch {
            scheduler.awaitPoint("point")
            resumed = true
        }
        runCurrent()
        
        scheduler.clear()
        runCurrent()
        assertTrue(resumed)
        
        // After clear, points should be new
        var resumed2 = false
        backgroundScope.launch {
            scheduler.awaitPoint("point")
            resumed2 = true
        }
        runCurrent()
        assertFalse(resumed2)
    }

}
