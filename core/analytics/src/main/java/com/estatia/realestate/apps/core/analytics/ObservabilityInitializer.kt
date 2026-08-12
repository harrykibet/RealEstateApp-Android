package com.estatia.realestate.apps.core.analytics

import com.estatia.realestate.apps.core.common.interfaces.IBackendInitializer
import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import com.estatia.realestate.apps.core.domain.interfaces.IAuthRepository
import com.estatia.realestate.apps.core.domain.interfaces.IConfigProvider
import com.estatia.realestate.apps.core.domain.interfaces.ICrashReporter
import io.micrometer.core.instrument.Metrics
import io.micrometer.core.instrument.logging.LoggingMeterRegistry
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

    override fun initialize() {
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
        }
    }
}
