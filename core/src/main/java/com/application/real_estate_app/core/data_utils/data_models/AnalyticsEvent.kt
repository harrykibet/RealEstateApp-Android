package com.application.real_estate_app.core.data_utils.data_models

data class AnalyticsEvent(
    val eventId: String,
    val eventType: String,
    val userId: String,
    val timestamp: Long,
    val metadata: Map<String, String>,
    val deviceInfo: DeviceInfo,
    val userLocation: UserLocation?
)

data class DeviceInfo(
    val os: String,
    val browser: String,
    val deviceType: String,
    val screenResolution: String,
    val appVersion: String
)

data class UserLocation(
    val country: String,
    val city: String,
    val latitude: Double,
    val longitude: Double
)
