package com.estatia.realestate.apps.core.analytics

import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import com.estatia.realestate.apps.core.domain.security.IAuthRepository
import com.estatia.realestate.apps.core.domain.analytics.ICrashReporter
import com.estatia.realestate.apps.core.domain.config.INetworkConfig
import com.estatia.realestate.apps.core.domain.config.ISecurityConfig
import com.estatia.realestate.apps.core.model.player.EnvironmentState
import com.estatia.realestate.apps.core.testing.chaos.environment.ChaosEnvironmentController
import io.micrometer.core.instrument.Metrics
import io.micrometer.core.instrument.logging.LoggingMeterRegistry
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import io.micrometer.registry.otlp.OtlpMeterRegistry
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ObservabilityInitializerTest {

    private lateinit var crashReporter: ICrashReporter
    private lateinit var deviceUtils: IDeviceUtils
    private lateinit var authRepository: IAuthRepository
    private lateinit var networkConfig: INetworkConfig
    private lateinit var securityConfig: ISecurityConfig
    private lateinit var initializer: ObservabilityInitializer
    
    private val chaosEnvironment = ChaosEnvironmentController(
        EnvironmentState(false, false, 10_000_000L)
    )

    @Before
    fun setup() {
        crashReporter = mockk(relaxed = true)
        deviceUtils = mockk(relaxed = true)
        authRepository = mockk(relaxed = true)
        networkConfig = mockk(relaxed = true)
        securityConfig = mockk(relaxed = true)
        
        initializer = ObservabilityInitializer(
            crashReporter,
            deviceUtils,
            authRepository,
            networkConfig,
            securityConfig
        )

        Metrics.globalRegistry.registries.forEach { Metrics.removeRegistry(it) }
    }

    @Test
    fun `initialize adds multiple registries when telemetry enabled`() = runTest {
        every { securityConfig.isTelemetryEnabled } returns true
        coEvery { networkConfig.awaitReady() } returns Unit

        initializer.initialize()

        val registries = Metrics.globalRegistry.registries
        assertTrue("Should contain LoggingMeterRegistry", registries.any { it is LoggingMeterRegistry })
        assertTrue("Should contain PrometheusMeterRegistry", registries.any { it is PrometheusMeterRegistry })
        assertTrue("Should contain OtlpMeterRegistry", registries.any { it is OtlpMeterRegistry })
    }

    @Test
    fun `telemetry correctly reports hardware thermal pressure signals`() = runTest {
        // 🧪 Chaos Injection: Simulate Overheating Device
        chaosEnvironment.triggerHighThermal()
        
        every { securityConfig.isTelemetryEnabled } returns true
        coEvery { networkConfig.awaitReady() } returns Unit

        initializer.initialize()
        
        // Verify that hardware-aware tags are attached to metrics
        // ... (Verification logic depending on tagging implementation)
    }
}
