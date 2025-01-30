package com.application.real_estate_app.feature_analytics.data.services

import com.application.real_estate_app.core.domain.models.AnalyticsEvent
import com.application.real_estate_app.core.domain.interfaces.AnalyticsRepoInterface
import android.content.Context
import com.application.real_estate_app.core.domain.interfaces.AuthRepoInterface
import com.application.real_estate_app.core.utils.system.DeviceInfoUtil
import com.application.real_estate_app.core.utils.system.LocationInfoUtil
import com.application.real_estate_app.feature_analytics.data.repositories.AnalyticsRepository
import javax.inject.Inject

class ImplAnalyticsCore @Inject constructor(
    private val analyticsApi: AnalyticsRepository,
    private val context: Context,
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
        val deviceInfo = DeviceInfoUtil.getDeviceInfo(context)
        val locationInfo = LocationInfoUtil.getLocationInfo(context)

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