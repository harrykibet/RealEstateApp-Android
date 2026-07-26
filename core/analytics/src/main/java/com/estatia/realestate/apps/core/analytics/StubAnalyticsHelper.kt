
package com.estatia.realestate.apps.core.analytics

import android.util.Log
import com.estatia.realestate.apps.core.data.interfaces.IAnalyticsTracker
import com.estatia.realestate.apps.core.model.analytics.AnalyticsEvent as FirebaseAnalyticsEvent

import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "StubAnalyticsHelper"

/**
 * An implementation of AnalyticsHelper just writes the events to logcat. Used in builds where no
 * analytics events should be sent to a backend.
 */
@Singleton
internal class StubAnalyticsHelper @Inject constructor(
    private val analyticsRepository: IAnalyticsTracker
) : AnalyticsHelper {
    override suspend fun logEvent(event: FirebaseAnalyticsEvent) {
        Log.d(TAG, "Received Firebase analytics event: $event")
        analyticsRepository.logEvent(event)
    }

    override suspend fun logEvent(event: AnalyticsEvent) {
        Log.d(TAG, "Received analytics event: $event")
    }
}
