package com.estatia.realestate.apps.core.analytics

import androidx.compose.runtime.staticCompositionLocalOf
import com.estatia.realestate.apps.core.model.analytics.AnalyticsEvent as FirebaseAnalyticsEvent

/**
 * Global key used to obtain access to the [IAnalyticsHelper] through a CompositionLocal.
 */
val LocalAnalyticsHelper = staticCompositionLocalOf<IAnalyticsHelper> {
    object : IAnalyticsHelper {
        override suspend fun logEvent(event: FirebaseAnalyticsEvent) {}
        override suspend fun logEvent(event: AnalyticsEvent) {}
        override suspend fun logPropertyScreenOpened(propertyId: String) {}
        override suspend fun logScreenView(screenName: String) {}
    }
}
