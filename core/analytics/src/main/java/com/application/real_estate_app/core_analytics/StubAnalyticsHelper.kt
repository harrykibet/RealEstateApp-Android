
package com.application.real_estate_app.core_analytics

import android.util.Log
import com.application.real_estate_app.core_data.interfaces.IAnalyticsRepository
import com.application.real_estate_app.core_model.analytics.AnalyticsEvent as FirebaseAnalyticsEvent
import com.application.real_estate_app.core_analytics.AnalyticsEvent

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
