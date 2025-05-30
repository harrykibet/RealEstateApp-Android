package com.estatia.realestate.apps.core.analytics

import com.estatia.realestate.apps.core.model.analytics.AnalyticsEvent as FirebaseAnalyticsEvent

/**
 * Implementation of AnalyticsHelper which does nothing. Useful for tests and previews.
 */
class NoOpAnalyticsHelper : AnalyticsHelper {
    override suspend fun logEvent(event: AnalyticsEvent) = Unit
    override suspend fun logEvent(event: FirebaseAnalyticsEvent) = Unit

}
