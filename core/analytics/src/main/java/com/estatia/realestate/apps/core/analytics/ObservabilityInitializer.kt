package com.estatia.realestate.apps.core.analytics

import com.estatia.realestate.apps.core.common.interfaces.IBackendInitializer
import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import com.estatia.realestate.apps.core.domain.interfaces.IAuthRepository
import com.estatia.realestate.apps.core.domain.interfaces.IConfigProvider
import com.estatia.realestate.apps.core.domain.interfaces.ICrashReporter
import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.Metrics
import io.micrometer.core.instrument.logging.LoggingMeterRegistry
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import io.micrometer.registry.otlp.OtlpConfig
import io.micrometer.registry.otlp.OtlpMeterRegistry
import javax.inject.Inject

/**
 * Initializes observability context (Crashlytics keys, metrics, etc.)
 */
internal class ObservabilityInitializer @Inject constructor(
    private val crashReporter: ICrashReporter,
    private val deviceUtils: IDeviceUtils,
    private val authRepository: IAuthRepository,
    private val configProvider: IConfigProvider
) : IBackendInitializer {

    override suspend fun initialize() {
        // Ensure config is ready before accessing it
        configProvider.awaitReady()

        // Set baseline crashlytics keys
        val deviceInfo = deviceUtils.getDeviceInfo()
        
        crashReporter.setCustomKey("os", deviceInfo.os)
        crashReporter.setCustomKey("device_type", deviceInfo.deviceType)
        crashReporter.setCustomKey("app_version", deviceInfo.appVersion)
        
        // Track user ID if already logged in
        authRepository.getCurrentUserId()?.let { userId ->
            crashReporter.setCustomKey("user_id", userId)
        }

        // 📊 Enable Metrics Egress for validation in debug builds OR if telemetry flag is enabled
        if (BuildConfig.DEBUG || configProvider.isTelemetryEnabled) {
            Metrics.addRegistry(LoggingMeterRegistry())
            
            // 📈 Real-world sinks for data-driven tuning
            setupAdvancedMetricsRegistries()
        }
    }

    private fun setupAdvancedMetricsRegistries() {
        // 1. Prometheus Registry (for local scraping/aggregation if needed)
        Metrics.addRegistry(PrometheusMeterRegistry(PrometheusConfig.DEFAULT))

        // 2. OTLP Registry (Push to OpenTelemetry Collector)
        val otlpConfig = object : OtlpConfig {
            override fun url(): String {
                val base = configProvider.baseUrl
                return if (base.endsWith("/")) "${base}v1/metrics" else "$base/v1/metrics"
            }
            override fun get(key: String): String? = null
            
            // Aggregation temporality must be CUMULATIVE for many OTLP backends
            override fun step(): java.time.Duration = java.time.Duration.ofMinutes(1)
        }
        
        Metrics.addRegistry(OtlpMeterRegistry(otlpConfig, Clock.SYSTEM))
    }
}
