package com.estatia.realestate.apps.core.player_engine.analytics

import com.estatia.realestate.apps.core.common.events.EventTypes
import com.estatia.realestate.apps.core.data.interfaces.IAnalyticsRepository
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

// Real-time error logging
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