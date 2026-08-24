package com.estatia.realestate.apps.core.common.system

import com.estatia.realestate.apps.core.testing.assertions.assertFirst
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PerformanceMonitorTest {

    private lateinit var monitor: PerformanceMonitor

    @Before
    fun setup() {
        monitor = PerformanceMonitor()
    }

    @Test
    fun `isJanking remains false for stable frames`() = runTest {
        // Report 30 non-janky frames
        repeat(30) {
            monitor.reportFrameData(isJanky = false)
        }

        assertFalse("Monitor should not report jank for stable frames", monitor.isJanking.value)
    }

    @Test
    fun `isJanking becomes true when jank ratio exceeds threshold`() = runTest {
        // threshold is 20% of 30 frames = 6 frames
        
        // Report 7 janky frames and 23 stable ones
        repeat(7) { monitor.reportFrameData(isJanky = true) }
        repeat(23) { monitor.reportFrameData(isJanky = false) }

        assertTrue("Monitor should flag janking when threshold exceeded", monitor.isJanking.value)
        
        // Reset window with stable frames
        repeat(30) { monitor.reportFrameData(isJanky = false) }
        assertFalse("Monitor should clear jank flag after window reset", monitor.isJanking.value)
    }

    @Test
    fun `isJanking handles rapid fluctuations in performance`() = runTest {
        // Window 1: High Jank
        repeat(15) { monitor.reportFrameData(isJanky = true) }
        repeat(15) { monitor.reportFrameData(isJanky = false) }
        assertTrue(monitor.isJanking.value)

        // Window 2: Recovery
        repeat(30) { monitor.reportFrameData(isJanky = false) }
        assertFalse(monitor.isJanking.value)

        // Window 3: Regression
        repeat(10) { monitor.reportFrameData(isJanky = true) }
        repeat(20) { monitor.reportFrameData(isJanky = false) }
        assertTrue(monitor.isJanking.value)
    }
}
