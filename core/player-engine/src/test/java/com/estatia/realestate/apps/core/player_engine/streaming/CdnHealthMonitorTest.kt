package com.estatia.realestate.apps.core.player_engine.streaming

import com.estatia.realestate.apps.core.model.cdn.CdnEndpoint
import com.estatia.realestate.apps.core.testing.clock.TestClock
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CdnHealthMonitorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private lateinit var measurer: ILatencyMeasurer
    private lateinit var monitor: CdnHealthMonitor
    private val testClock = TestClock(1000L)
    private val endpoint = CdnEndpoint("Test", "127.0.0.1")

    @Before
    fun setup() {
        measurer = mockk()
        monitor = CdnHealthMonitor(measurer, testScope, testDispatcher, clock = { testClock.currentTimeMillis() })
    }

    @Test
    fun `refreshIfStale performs measurement in background`() = runTest(testDispatcher) {
        coEvery { measurer.measure(any(), any()) } returns 50L

        monitor.refreshIfStale(listOf(endpoint))
        testScope.advanceUntilIdle()

        val health = monitor.getHealthSnapshot()[endpoint.baseUrl]
        
        assertNotNull(health)
        assertEquals(50L, health?.latencyMs)
        assertEquals(0, health?.failureCount)
    }

    @Test
    fun `reportExternalFailure increments failure count and trips circuit breaker`() = runTest(testDispatcher) {
        val baseUrl = endpoint.baseUrl
        
        repeat(3) {
            monitor.reportExternalFailure(baseUrl)
        }

        val health = monitor.getHealthSnapshot()[baseUrl]
        assertNotNull(health)
        assertEquals(3, health?.failureCount)
        assertEquals(true, health?.isCircuitOpen)
        
        // Advance time beyond circuit open duration (60s)
        testClock.advanceBy(61_000L)
        
        val healthAfter = monitor.getHealthSnapshot()[baseUrl]
        assertEquals(false, healthAfter?.isCircuitOpen)
    }
}
