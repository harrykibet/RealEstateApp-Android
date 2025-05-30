
package com.estatia.realestate.apps.core.analytics

import android.util.Log
import com.estatia.realestate.apps.core.data.interfaces.IAnalyticsRepository
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
    private val analyticsRepository: IAnalyticsRepository
) : AnalyticsHelper {
    override suspend fun logEvent(event: FirebaseAnalyticsEvent) {
        analyticsRepository.logEvent(event){
            Log.e(TAG, "Error logging event: $event", it)
        }
    }

    override suspend fun logEvent(event: AnalyticsEvent) {
        Log.d(TAG, "Received analytics event: $event")
    }
}
