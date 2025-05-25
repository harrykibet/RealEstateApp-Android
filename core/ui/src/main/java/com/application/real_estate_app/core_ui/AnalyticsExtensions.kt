package com.application.real_estate_app.core_ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.application.real_estate_app.core_analytics.AnalyticsEvent
import com.application.real_estate_app.core_analytics.AnalyticsHelper
import com.application.real_estate_app.core_analytics.LocalAnalyticsHelper
import com.application.real_estate_app.core_analytics.AnalyticsEvent.Param
import com.application.real_estate_app.core_analytics.AnalyticsEvent.ParamKeys
import com.application.real_estate_app.core_analytics.AnalyticsEvent.Types

/**
 * Classes and functions associated with analytics events for the UI.
 */
suspend fun AnalyticsHelper.logScreenView(screenName: String) {
    logEvent(
        AnalyticsEvent(
            type = Types.SCREEN_VIEW,
            extras = listOf(
                Param(ParamKeys.SCREEN_NAME, screenName),
            ),
        ),
    )
}

suspend fun AnalyticsHelper.logNewsResourceOpened(newsResourceId: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "news_resource_opened",
            extras = listOf(
                Param("opened_news_resource", newsResourceId),
            ),
        ),
    )
}

/**
 * A side-effect which records a screen view event.
 */
@Composable
fun TrackScreenViewEvent(
    screenName: String,
    analyticsHelper: AnalyticsHelper = LocalAnalyticsHelper.current,
) {
    LaunchedEffect(screenName) {
        analyticsHelper.logScreenView(screenName)
    }
}
