package com.estatia.realestate.apps.core.analytics

import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import com.estatia.realestate.apps.core.domain.security.IAuthRepository
import com.estatia.realestate.apps.core.domain.analytics.ICrashReporter
import com.estatia.realestate.apps.core.domain.config.INetworkConfig
import com.estatia.realestate.apps.core.domain.config.ISecurityConfig
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

        // Clear global registry before each test
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
    fun `initialize does not add advanced registries when telemetry disabled`() = runTest {
        every { securityConfig.isTelemetryEnabled } returns false
        coEvery { networkConfig.awaitReady() } returns Unit

        initializer.initialize()

        val registries = Metrics.globalRegistry.registries
        // In unit tests, BuildConfig.DEBUG is false usually, so only check if advanced are MISSING
        assertTrue("Should NOT contain PrometheusMeterRegistry", registries.none { it is PrometheusMeterRegistry })
        assertTrue("Should NOT contain OtlpMeterRegistry", registries.none { it is OtlpMeterRegistry })
    }
}
