package com.application.real_estate_app.core_analytics

import com.application.real_estate_app.core_model.analytics.AnalyticsEvent as FirebaseAnalyticsEvent

/**
 * Implementation of AnalyticsHelper which does nothing. Useful for tests and previews.
 */
class NoOpAnalyticsHelper : AnalyticsHelper {
    override suspend fun logEvent(event: AnalyticsEvent) = Unit
    override suspend fun logEvent(event: FirebaseAnalyticsEvent) = Unit

}
