package com.estatia.realestate.apps.core.player_engine.utils

import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import com.estatia.realestate.apps.core.domain.config.IPlayerTuningConfig
import com.estatia.realestate.apps.core.model.config.PlayerTuningConfig
import com.estatia.realestate.apps.core.model.player.EnvironmentState
import com.estatia.realestate.apps.core.model.property.MediaType
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DynamicBitratePolicyTest {

    private lateinit var deviceUtils: IDeviceUtils
    private lateinit var config: IPlayerTuningConfig
    private lateinit var policy: DynamicBitratePolicy

    private val tuning = PlayerTuningConfig()

    private val defaultEnvironment = EnvironmentState(
        isMetered = false,
        shouldThrottlePerformance = false,
        estimatedThroughputBps = 10_000_000L
    )

    @Before
    fun setup() {
        deviceUtils = mockk {
            every { getMaxSupportedBitrate() } returns 20_000_000
            every { isLowRamDevice() } returns false
        }
        config = mockk {
            every { playerTuning } returns tuning
        }
        policy = DynamicBitratePolicy(deviceUtils, config)
    }

    @Test
    fun `bitrate reduces significantly under critical thermal pressure`() {
        val normalBitrate = policy.calculateMaxVideoBitrate(MediaType.VOD, defaultEnvironment)
        
        // 🧪 Chaos: Simulate overheating (THERMAL_STATUS_CRITICAL = 4)
        val hotEnvironment = defaultEnvironment.copy(thermalStatus = 4)
        val throttledBitrate = policy.calculateMaxVideoBitrate(MediaType.VOD, hotEnvironment)

        assertTrue("Bitrate should drop by > 80% under critical thermal", throttledBitrate < (normalBitrate * 0.2))
    }

    @Test
    fun `bitrate drops when buffer is below precautionary threshold`() {
        val highBufferBitrate = policy.calculateMaxVideoBitrate(MediaType.VOD, defaultEnvironment, bufferSeconds = 10.0)
        
        // Below precautionary threshold (default 5.0s)
        val lowBufferBitrate = policy.calculateMaxVideoBitrate(MediaType.VOD, defaultEnvironment, bufferSeconds = 3.0)

        assertTrue("Bitrate should drop when buffer is low", lowBufferBitrate < highBufferBitrate)
    }

    @Test
    fun `bitrate remains above minimum safe floor even under extreme chaos`() {
        // 🧪 Chaos: Everything fails
        val chaosEnv = defaultEnvironment.copy(
            isMetered = true,
            shouldThrottlePerformance = true,
            thermalStatus = 4,
            estimatedThroughputBps = 100L // Near zero network
        )
        
        val result = policy.calculateMaxVideoBitrate(MediaType.LIVE, chaosEnv, bufferSeconds = 0.1)
        
        // Standard floor is 500kbps
        assertTrue("Bitrate floor must be respected", result >= 500_000)
    }
}
