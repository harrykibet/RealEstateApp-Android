package com.estatia.realestate.apps.core.analytics

import com.estatia.realestate.apps.core.model.analytics.AnalyticsEvent as FirebaseAnalyticsEvent

/**
 * Interface for logging analytics events. See `FirebaseAnalyticsHelper` and
 * `StubAnalyticsHelper` for implementations.
 */
interface AnalyticsHelper {
    suspend fun logEvent(event: FirebaseAnalyticsEvent)
    suspend fun logEvent(event: AnalyticsEvent)
}
