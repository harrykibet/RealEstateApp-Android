package com.estatia.realestate.apps.core.analytics

import android.util.Log
import com.estatia.realestate.apps.core.domain.interfaces.IAnalyticsTracker
import com.estatia.realestate.apps.core.model.analytics.AnalyticsEvent as FirebaseAnalyticsEvent
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "StubAnalyticsHelper"


@Singleton
class AnalyticsHelper @Inject constructor(
    private val analyticsRepository: IAnalyticsTracker
) : IAnalyticsHelper {
    override suspend fun logEvent(event: FirebaseAnalyticsEvent) {
        Log.d(TAG, "Received Firebase analytics event: $event")
        analyticsRepository.logEvent(event)
    }

    override suspend fun logEvent(event: AnalyticsEvent) {
        Log.d(TAG, "Received analytics event: $event")
    }

    override suspend fun logPropertyScreenOpened(propertyId: String) {
        Log.d(TAG, "Property screen opened: $propertyId")
    }

    override suspend fun logScreenView(screenName: String) {
        Log.d(TAG, "Screen view: $screenName")
    }
}
