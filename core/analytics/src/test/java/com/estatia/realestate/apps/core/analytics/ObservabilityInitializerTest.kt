package com.estatia.realestate.apps.core.analytics

import com.estatia.realestate.apps.core.common.interfaces.BuildEnvironment
import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import com.estatia.realestate.apps.core.domain.security.IAuthRepository
import com.estatia.realestate.apps.core.domain.analytics.ICrashReporter
import com.estatia.realestate.apps.core.domain.config.INetworkConfig
import com.estatia.realestate.apps.core.domain.config.ISecurityConfig
import com.estatia.realestate.apps.core.model.player.EnvironmentState
import com.estatia.realestate.apps.core.testing.chaos.environment.ChaosEnvironmentController
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.logging.LoggingMeterRegistry
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import io.micrometer.registry.otlp.OtlpConfig
import io.micrometer.registry.otlp.OtlpMeterRegistry
import io.micrometer.core.instrument.Clock
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ObservabilityInitializerTest {

    private lateinit var crashReporter: ICrashReporter
    private lateinit var deviceUtils: IDeviceUtils
    private lateinit var authRepository: IAuthRepository
    private lateinit var networkConfig: INetworkConfig
    private lateinit var securityConfig: ISecurityConfig
    private lateinit var registrar: FakeMetricsRegistrar
    private lateinit var initializer: ObservabilityInitializer
    
    private val chaosEnvironment = ChaosEnvironmentController(
        EnvironmentState(false, false, 10_000_000L)
    )

    private val testRegistries = setOf(
        LoggingMeterRegistry(),
        PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    )

    @Before
    fun setup() {
        crashReporter = mockk(relaxed = true)
        deviceUtils = mockk(relaxed = true)
        authRepository = mockk(relaxed = true)
        networkConfig = mockk(relaxed = true)
        securityConfig = mockk(relaxed = true)
        registrar = FakeMetricsRegistrar()
    }

    @Test
    fun `initialize adds multiple registries when telemetry enabled`() = runTest {
        every { securityConfig.isTelemetryEnabled } returns true
        coEvery { networkConfig.awaitReady() } returns Unit
        
        initializer = createInitializer(isDebug = false)
        initializer.initialize()

        assertEquals(testRegistries.size, registrar.registered.size)
        assertTrue(registrar.registered.any { it is LoggingMeterRegistry })
        assertTrue(registrar.registered.any { it is PrometheusMeterRegistry })
    }

    @Test
    fun `initialize adds registries when in debug mode regardless of telemetry flag`() = runTest {
        every { securityConfig.isTelemetryEnabled } returns false
        coEvery { networkConfig.awaitReady() } returns Unit
        
        initializer = createInitializer(isDebug = true)
        initializer.initialize()

        assertEquals(testRegistries.size, registrar.registered.size)
    }

    @Test
    fun `initialize does not add registries when telemetry disabled and not in debug`() = runTest {
        every { securityConfig.isTelemetryEnabled } returns false
        coEvery { networkConfig.awaitReady() } returns Unit
        
        initializer = createInitializer(isDebug = false)
        initializer.initialize()

        assertTrue(registrar.registered.isEmpty())
    }

    private fun createInitializer(isDebug: Boolean) = ObservabilityInitializer(
        crashReporter,
        deviceUtils,
        authRepository,
        networkConfig,
        securityConfig,
        testRegistries,
        registrar,
        FakeBuildEnvironment(isDebug)
    )

    class FakeMetricsRegistrar : MetricsRegistrar {
        val registered = mutableListOf<MeterRegistry>()
        override fun register(registry: MeterRegistry) {
            registered += registry
        }
    }

    class FakeBuildEnvironment(override val isDebug: Boolean) : BuildEnvironment
}
