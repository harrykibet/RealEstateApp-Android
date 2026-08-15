package com.estatia.realestate.apps.core.analytics

import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import com.estatia.realestate.apps.core.domain.interfaces.IAuthRepository
import com.estatia.realestate.apps.core.domain.interfaces.IConfigProvider
import com.estatia.realestate.apps.core.domain.interfaces.ICrashReporter
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
    private lateinit var configProvider: IConfigProvider
    private lateinit var initializer: ObservabilityInitializer

    @Before
    fun setup() {
        crashReporter = mockk(relaxed = true)
        deviceUtils = mockk(relaxed = true)
        authRepository = mockk(relaxed = true)
        configProvider = mockk(relaxed = true)
        
        initializer = ObservabilityInitializer(
            crashReporter,
            deviceUtils,
            authRepository,
            configProvider
        )

        // Clear global registry before each test
        Metrics.globalRegistry.registries.forEach { Metrics.removeRegistry(it) }
    }

    @Test
    fun `initialize adds multiple registries when telemetry enabled`() = runTest {
        every { configProvider.isTelemetryEnabled } returns true
        coEvery { configProvider.awaitReady() } returns Unit

        initializer.initialize()

        val registries = Metrics.globalRegistry.registries
        assertTrue("Should contain LoggingMeterRegistry", registries.any { it is LoggingMeterRegistry })
        assertTrue("Should contain PrometheusMeterRegistry", registries.any { it is PrometheusMeterRegistry })
        assertTrue("Should contain OtlpMeterRegistry", registries.any { it is OtlpMeterRegistry })
    }

    @Test
    fun `initialize does not add advanced registries when telemetry disabled`() = runTest {
        every { configProvider.isTelemetryEnabled } returns false
        coEvery { configProvider.awaitReady() } returns Unit

        initializer.initialize()

        val registries = Metrics.globalRegistry.registries
        // In unit tests, BuildConfig.DEBUG is false usually, so only check if advanced are MISSING
        assertTrue("Should NOT contain PrometheusMeterRegistry", registries.none { it is PrometheusMeterRegistry })
        assertTrue("Should NOT contain OtlpMeterRegistry", registries.none { it is OtlpMeterRegistry })
    }
}
