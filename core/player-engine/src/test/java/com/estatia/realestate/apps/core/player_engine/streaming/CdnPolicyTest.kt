package com.estatia.realestate.apps.core.player_engine.streaming

import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.model.cdn.CdnEndpoint
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

@UnstableApi
class CdnPolicyTest {

    private lateinit var environmentCoordinator: EnvironmentCoordinator
    private lateinit var healthMonitor: CdnHealthMonitor
    private lateinit var random: Random
    private lateinit var policy: CdnPolicy

    private val environmentFlow = MutableStateFlow(
        EnvironmentState(isMetered = false, shouldThrottlePerformance = false, estimatedThroughputBps = 1000)
    )

    private val endpoints = listOf(
        CdnEndpoint("A", "https://cdn-a.com"),
        CdnEndpoint("B", "https://cdn-b.com")
    )

    @Before
    fun setup() {
        environmentCoordinator = mockk {
            every { environment } returns environmentFlow
        }
        healthMonitor = mockk()
        random = mockk {
            every { nextInt(any()) } returns 0
        }
        policy = CdnPolicy(environmentCoordinator, random)
    }

    @Test
    fun `select stable network choose best latency`() = runTest {
        // Given
        coEvery { healthMonitor.getHealth(endpoints[0]) } returns CdnHealth(latencyMs = 100, 0, 0, null)
        coEvery { healthMonitor.getHealth(endpoints[1]) } returns CdnHealth(latencyMs = 50, 0, 0, null)

        // When
        val selected = policy.select(endpoints, healthMonitor)

        // Then
        assertEquals(endpoints[1], selected)
    }

    @Test
    fun `select metered network choose random fallback`() = runTest {
        // Given
        environmentFlow.value = environmentFlow.value.copy(isMetered = true)

        // When
        val selected = policy.select(endpoints, healthMonitor)

        // Then
        assertEquals(endpoints[0], selected) // random index 0 mocked
    }

    @Test
    fun `select with open circuit skips that endpoint`() = runTest {
        // Given
        coEvery { healthMonitor.getHealth(endpoints[0]) } returns CdnHealth(latencyMs = 10, 0, 0, System.currentTimeMillis() + 10000)
        coEvery { healthMonitor.getHealth(endpoints[1]) } returns CdnHealth(latencyMs = 50, 0, 0, null)

        // When
        val selected = policy.select(endpoints, healthMonitor)

        // Then
        assertEquals(endpoints[1], selected)
    }
}
