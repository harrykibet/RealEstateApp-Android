package com.application.real_estate_app.core_analytics.data.services

import com.application.real_estate_app.core_analytics.data.repositories.AnalyticsRepository
import com.application.real_estate_app.core_interface.AnalyticsRepoInterface
import com.application.real_estate_app.core_interface.AuthRepoInterface
import com.application.real_estate_app.core_interface.IDeviceUtils
import com.application.real_estate_app.core_interface.ILocationUtils
import com.application.real_estate_app.core_model.AnalyticsEvent
import javax.inject.Inject

class ImplAnalyticsCore @Inject constructor(
    private val analyticsApi: AnalyticsRepository,
    private val deviceUtils: IDeviceUtils,
    private val locationUtils: ILocationUtils,
    private val authApi: AuthRepoInterface
) : AnalyticsRepoInterface {

    override suspend fun logEvent(
    message: String,
    eventType: String,
    customMetadata: Map<String, String>?,
    onFailure: (Exception) -> Unit
    ): Boolean
    {
        val metadata = customMetadata?.toMutableMap() ?: mutableMapOf()
        metadata["message"] = message  // Add default message if not already present
        val deviceInfo = deviceUtils.getDeviceInfo()
        val locationInfo = locationUtils.getLocationInfo()

        val analyticsEvent = authApi.getCurrentUserId()?.let {
            AnalyticsEvent(
                eventId = analyticsApi.generateEventId(),
                eventType = eventType,
                userId = it,
                timestamp = System.currentTimeMillis(),
                metadata = metadata,
                deviceInfo = deviceInfo,
                userLocation = locationInfo
            )
        }
        return analyticsApi.logEvent(analyticsEvent!!, onFailure)
    }
}