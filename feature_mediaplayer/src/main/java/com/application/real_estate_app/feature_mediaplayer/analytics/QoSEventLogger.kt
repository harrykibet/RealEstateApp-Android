package com.application.real_estate_app.feature_mediaplayer.analytics

import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

// Real-time error logging
@Suppress("Unused")
@Singleton
class QoSEventLogger @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) {
    private val errorCodes = ConcurrentHashMap<String, Int>()

    fun logCDNFailure(cdnUrl: String, responseCode: Int) {
        firebaseAnalytics.logEvent("cdn_failure", bundleOf(
            "cdn_url" to cdnUrl,
            "response_code" to responseCode
        ))
        errorCodes.compute(cdnUrl) { _, v -> (v ?: 0) + 1 }
    }

    fun getErrorRate(cdnUrl: String): Double {
        return errorCodes[cdnUrl]?.toDouble() ?: 0.0
    }
}