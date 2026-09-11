package com.estatia.realestate.apps.core.model.analytics

import com.estatia.realestate.apps.core.model.system.DeviceInfo
import com.estatia.realestate.apps.core.model.user.UserLocation
import kotlinx.serialization.Serializable
import com.estatia.realestate.apps.core.architecture.annotations.Data.AnalyticsState

@Serializable
@AnalyticsState
data class AnalyticsEvent(
    val eventId: String,
    val eventType: String,
    val userId: String,
    val timestamp: Long,
    val metadata: Map<String, String>,
    val deviceInfo: DeviceInfo,
    val userLocation: UserLocation?
)




