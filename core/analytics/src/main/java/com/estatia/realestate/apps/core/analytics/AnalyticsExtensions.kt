package com.estatia.realestate.apps.core.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.staticCompositionLocalOf
import com.estatia.realestate.apps.core.analytics.AnalyticsEvent.Param
import com.estatia.realestate.apps.core.analytics.AnalyticsEvent.ParamKeys
import com.estatia.realestate.apps.core.analytics.AnalyticsEvent.Types
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

/**
 * Classes and functions to help with logging analytics events.
 */

@Composable
fun TrackScreenViewEvent(
    screenName: String,
    analyticsHelper: IAnalyticsHelper
) {
    LaunchedEffect(screenName) {
        analyticsHelper.logEvent(
            AnalyticsEvent(
                type = Types.SCREEN_VIEW,
                extras = listOf(
                    Param(ParamKeys.SCREEN_NAME, screenName),
                ),
            ),
        )
    }
}
