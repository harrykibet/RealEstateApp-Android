package com.estatia.realestate.apps.core.analytics

import com.estatia.realestate.apps.core.common.interfaces.BuildEnvironment
import com.estatia.realestate.apps.core.common.interfaces.IBackendInitializer
import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import com.estatia.realestate.apps.core.domain.security.IAuthRepository
import com.estatia.realestate.apps.core.domain.analytics.ICrashReporter
import com.estatia.realestate.apps.core.domain.config.INetworkConfig
import com.estatia.realestate.apps.core.domain.config.ISecurityConfig
import io.micrometer.core.instrument.MeterRegistry
import javax.inject.Inject

/**
 * Initializes the global observability context (Crashlytics, Metrics, Tracing).
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Setup monitoring infrastructure and set baseline device context.
 * - Concurrency: Thread-safe; idempotent initialization.
 * - Resilience: Blocks until [networkConfig] is ready to ensure correct OTLP URLs.
 * - Performance: Avoids blocking the main thread during heavy registry setup.
 */
internal class ObservabilityInitializer @Inject constructor(
    private val crashReporter: ICrashReporter,
    private val deviceUtils: IDeviceUtils,
    private val authRepository: IAuthRepository,
    private val networkConfig: INetworkConfig,
    private val securityConfig: ISecurityConfig,
    private val meterRegistries: Set<@JvmSuppressWildcards MeterRegistry>,
    private val metricsRegistrar: MetricsRegistrar,
    private val buildEnvironment: BuildEnvironment
) : IBackendInitializer {

    override suspend fun initialize() {
        // Ensure config is ready before accessing it
        networkConfig.awaitReady()

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
        if (buildEnvironment.isDebug || securityConfig.isTelemetryEnabled) {
            meterRegistries.forEach(metricsRegistrar::register)
        }
    }
}
