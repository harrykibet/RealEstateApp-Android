package com.estatia.realestate.apps.core.player_engine.streaming

import com.estatia.realestate.apps.core.model.cdn.CdnEndpoint
import com.estatia.realestate.apps.core.testing.clock.TestClock
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CdnHealthMonitorTest {

    private lateinit var measurer: ILatencyMeasurer
    private val testClock = TestClock(1000L)
    private val endpoint = CdnEndpoint("Test", "127.0.0.1")

    @Before
    fun setup() {
        measurer = mockk()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `refreshIfStale performs measurement in background`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val monitor = CdnHealthMonitor(
            latencyMeasurer = measurer,
            scope = this, // Use the test scope itself
            ioDispatcher = dispatcher,
            clock = { testClock.currentTimeMillis() }
        )
        
        coEvery { measurer.measure(any(), any()) } returns 50L

        monitor.refreshIfStale(listOf(endpoint))
        
        // Fully flush the scheduler to ensure all launched jobs and withContext blocks finish
        advanceUntilIdle()

        val health = monitor.getHealthSnapshot()[endpoint.baseUrl]
        
        assertNotNull("Health snapshot should not be null after refresh", health)
        assertEquals(50L, health?.latencyMs)
        assertEquals(0, health?.failureCount)
    }

    @Test
    fun `reportExternalFailure increments failure count and trips circuit breaker`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val monitor = CdnHealthMonitor(
            latencyMeasurer = measurer,
            scope = this,
            ioDispatcher = dispatcher,
            clock = { testClock.currentTimeMillis() }
        )
        val baseUrl = endpoint.baseUrl
        
        repeat(3) {
            monitor.reportExternalFailure(baseUrl)
        }

        val health = monitor.getHealthSnapshot()[baseUrl]
        assertNotNull(health)
        assertEquals(3, health?.failureCount)
        assertEquals(true, health?.isCircuitOpen(testClock.currentTimeMillis()))
        
        // 🧪 Deterministic Time Advancement:
        // Advance time beyond circuit open duration (60s)
        testClock.advanceBy(61_000L)
        
        val healthAfter = monitor.getHealthSnapshot()[baseUrl]
        assertEquals(false, healthAfter?.isCircuitOpen(testClock.currentTimeMillis()))
    }

    @Test
    fun `monitor handles measurement timeout chaos gracefully`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val monitor = CdnHealthMonitor(
            latencyMeasurer = measurer,
            scope = this,
            ioDispatcher = dispatcher,
            clock = { testClock.currentTimeMillis() }
        )
        
        // 🧪 Chaos Scenario: Latency measurement times out
        coEvery { measurer.measure(any(), any()) } throws java.net.SocketTimeoutException("Timeout")

        monitor.refreshIfStale(listOf(endpoint))
        
        // Fully flush the scheduler
        advanceUntilIdle()

        val health = monitor.getHealthSnapshot()[endpoint.baseUrl]
        assertNotNull("Health snapshot should not be null even after failure", health)
        assertEquals(1, health?.failureCount)
    }
}
