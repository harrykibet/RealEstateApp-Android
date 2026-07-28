package com.estatia.realestate.apps.core.player_engine.streaming

import com.estatia.realestate.apps.core.model.cdn.CdnEndpoint
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CdnHealthMonitorTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var measurer: ILatencyMeasurer
    private lateinit var monitor: CdnHealthMonitor
    private val endpoint = CdnEndpoint("Test", "127.0.0.1")

    @Before
    fun setup() {
        measurer = mockk()
        monitor = CdnHealthMonitor(measurer, testDispatcher)
    }

    @Test
    fun `getHealth performs measurement on first call`() = runTest(testDispatcher) {
        coEvery { measurer.measure(any(), any()) } returns 50L

        val health = monitor.getHealth(endpoint)
        
        assertNotNull(health)
        assertEquals(50L, health.latencyMs)
        assertEquals(0, health.failureCount)
    }

    @Test
    fun `getHealth returns cached value within TTL`() = runTest(testDispatcher) {
        coEvery { measurer.measure(any(), any()) } returns 50L

        val health1 = monitor.getHealth(endpoint)
        val health2 = monitor.getHealth(endpoint)

        assertEquals(health1.lastCheckedAt, health2.lastCheckedAt)
    }

    @Test
    fun `getHealth records failure when measurement throws`() = runTest(testDispatcher) {
        coEvery { measurer.measure(any(), any()) } throws RuntimeException("Network Error")

        val health = monitor.getHealth(endpoint)

        assertEquals(null, health.latencyMs)
        assertEquals(1, health.failureCount)
    }
}
