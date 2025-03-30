package com.application.real_estate_app.core_model

data class AnalyticsEvent(
    val eventId: String,
    val eventType: String,
    val userId: String,
    val timestamp: Long,
    val metadata: Map<String, String>,
    val deviceInfo: DeviceInfo,
    val userLocation: UserLocation?
)




