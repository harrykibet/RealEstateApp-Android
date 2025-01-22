package com.application.real_estate_app.feature_analytics.data.services

import com.application.real_estate_app.core.data_utils.data_models.AnalyticsEvent
import com.application.real_estate_app.core.interfaces.AnalyticsApiInterface
import android.content.Context
import com.application.real_estate_app.core.interfaces.AuthApiInterface
import com.application.real_estate_app.core.system_utils.DeviceInfoUtil
import com.application.real_estate_app.core.system_utils.LocationInfoUtil
import com.application.real_estate_app.feature_analytics.data.apis.AnalyticsApi
import javax.inject.Inject

class ImplAnalyticsCore @Inject constructor(
    private val analyticsApi: AnalyticsApi,
    private val context: Context,
    private val authApi: AuthApiInterface
) : AnalyticsApiInterface {

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