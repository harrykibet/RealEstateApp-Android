package com.estatia.realestate.apps.core.network.sources.firebase

import android.os.Bundle
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.AuthException
import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import com.estatia.realestate.apps.core.common.interfaces.ILocationUtils
import com.estatia.realestate.apps.core.model.analytics.AnalyticsEvent
import com.estatia.realestate.apps.core.network.interfaces.IAnalyticsRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.IAuthRemoteDataSource
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.UUID
import javax.inject.Inject


internal class FirestoreAnalytics @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val deviceUtils: IDeviceUtils,
    private val authService: IAuthRemoteDataSource,
    private val locationUtils: ILocationUtils
) : IAnalyticsRemoteDataSource {


    override suspend fun logEvent(
        event: AnalyticsEvent
    ): AppResult<Unit> {

        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, event.eventId)
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, event.eventType)
            putString("user_id", event.userId)
            putLong("timestamp", event.timestamp)
            
            event.metadata.forEach { (key, value) ->
                putString("meta_$key", value)
            }

            val device = event.deviceInfo
            putString("device_os", device.os)
            putString("app_version", device.appVersion)
            putString("device_type", device.deviceType)

            event.userLocation?.let { loc ->
                putDouble("latitude", loc.latitude)
                putDouble("longitude", loc.longitude)
            }
        }

        firebaseAnalytics.logEvent(event.eventType, params)
        
        return AppResult.Success(Unit)
    }


    override suspend fun logEvent(
        message: String,
        eventType: String,
        customMetadata: Map<String, String>?
    ): AppResult<Unit> {

        val userId =
            authService.getCurrentUserId()
                ?: return AppResult.Error(
                    AuthException.UserNotAuthenticated
                )


        val metadata =
            customMetadata
                ?.toMutableMap()
                ?: mutableMapOf()


        metadata["message"] = message


        val event = AnalyticsEvent(
            eventId = generateEventId(),
            eventType = eventType,
            userId = userId,
            timestamp = System.currentTimeMillis(),
            metadata = metadata,
            deviceInfo = deviceUtils.getDeviceInfo(),
            userLocation = locationUtils.getLocationInfo()
        )


        return logEvent(event)
    }


    @Deprecated("Raw event querying is no longer supported on the client.")
    override suspend fun getEventsForUser(
        userId: String
    ): AppResult<List<AnalyticsEvent>> {
        // Firebase Analytics SDK is write-only from client. 
        // For scale, raw events should be queried via BigQuery.
        return AppResult.Success(emptyList())
    }


    @Deprecated("Raw event querying is no longer supported on the client.")
    override suspend fun getEventById(
        eventId: String
    ): AppResult<AnalyticsEvent?> {
        return AppResult.Success(null)
    }


    override fun generateEventId(): String {
        return UUID.randomUUID().toString()
    }
}
