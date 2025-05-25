package com.application.real_estate_app.core_analytics

import com.application.real_estate_app.core_model.analytics.AnalyticsEvent as FirebaseAnalyticsEvent
import com.application.real_estate_app.core_analytics.AnalyticsEvent

/**
 * Interface for logging analytics events. See `FirebaseAnalyticsHelper` and
 * `StubAnalyticsHelper` for implementations.
 */
interface AnalyticsHelper {
    suspend fun logEvent(event: FirebaseAnalyticsEvent)
    suspend fun logEvent(event: AnalyticsEvent)
}
