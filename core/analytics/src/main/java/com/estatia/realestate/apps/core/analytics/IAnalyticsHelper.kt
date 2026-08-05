package com.estatia.realestate.apps.core.analytics

import androidx.compose.runtime.Composable
import com.estatia.realestate.apps.core.model.analytics.AnalyticsEvent as FirebaseAnalyticsEvent


interface IAnalyticsHelper {
    suspend fun logEvent(event: FirebaseAnalyticsEvent)
    suspend fun logEvent(event: AnalyticsEvent)

    suspend fun logPropertyScreenOpened(propertyId: String)
    suspend fun logScreenView(screenName: String)
}
