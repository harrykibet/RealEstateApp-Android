package com.estatia.realestate.apps.core.analytics

import com.estatia.realestate.apps.core.domain.interfaces.IAnalyticsTracker
import com.estatia.realestate.apps.core.model.analytics.AnalyticsEvent as FirebaseAnalyticsEvent
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsHelper @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val analyticsRepository: IAnalyticsTracker
) : IAnalyticsHelper {

    override suspend fun logEvent(event: FirebaseAnalyticsEvent) {
        // Send to Firebase
        firebaseAnalytics.logEvent(event.eventType) {
            event.metadata.forEach { (key, value) ->
                param(key, value)
            }
        }
        // Also log to our tracker (e.g. for backend sync or local logging)
        analyticsRepository.logEvent(event)
    }

    override suspend fun logEvent(event: AnalyticsEvent) {
        firebaseAnalytics.logEvent(event.type) {
            event.extras.forEach { param ->
                param(param.key, param.value)
            }
        }
    }

    override suspend fun logPropertyScreenOpened(propertyId: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID, propertyId)
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "property")
        }
    }

    override suspend fun logScreenView(screenName: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        }
    }
}
