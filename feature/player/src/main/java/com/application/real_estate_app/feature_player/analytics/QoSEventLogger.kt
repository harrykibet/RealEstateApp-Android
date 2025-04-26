package com.application.real_estate_app.feature_player.analytics

import com.application.real_estate_app.core_common.events.EventTypes
import com.application.real_estate_app.core_data.interfaces.IAnalyticsRepository
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

// Real-time error logging
@Suppress("Unused")
@Singleton
class QoSEventLogger @Inject constructor(
    private val analyticsClient: IAnalyticsRepository,
) {
    private val errorCodes = ConcurrentHashMap<String, Int>()

    suspend fun logCDNFailure(cdnUrl: String, responseCode: Int) {
        analyticsClient.logEvent(message = "$cdnUrl : $responseCode",
            eventType = EventTypes.EVENT_CDN_FAILURE,
            onFailure = {
                // Handle Failure
            })
        errorCodes.compute(cdnUrl) { _, v -> (v ?: 0) + 1 }
    }

    fun getErrorRate(cdnUrl: String): Double {
        return errorCodes[cdnUrl]?.toDouble() ?: 0.0
    }
}