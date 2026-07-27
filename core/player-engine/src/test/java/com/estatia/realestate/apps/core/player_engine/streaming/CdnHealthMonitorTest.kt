package com.estatia.realestate.apps.core.player_engine.streaming

import com.estatia.realestate.apps.core.model.cdn.CdnEndpoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CdnHealthMonitorTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var monitor: CdnHealthMonitor
    private val endpoint = CdnEndpoint("Test", "127.0.0.1")

    @Before
    fun setup() {
        monitor = CdnHealthMonitor(testDispatcher)
    }

    @Test
    fun `getHealth performs measurement on first call`() = runTest {
        // This will likely fail in measurement but we want to check the resulting state
        val health = monitor.getHealth(endpoint)
        
        assertNotNull(health)
        assertEquals(1, health.failureCount.coerceAtMost(1)) // measurement might fail or timeout
    }

    @Test
    fun `getHealth returns cached value within TTL`() = runTest {
        // Call twice
        val health1 = monitor.getHealth(endpoint)
        val health2 = monitor.getHealth(endpoint)

        assertEquals(health1.lastCheckedAt, health2.lastCheckedAt)
    }
}
