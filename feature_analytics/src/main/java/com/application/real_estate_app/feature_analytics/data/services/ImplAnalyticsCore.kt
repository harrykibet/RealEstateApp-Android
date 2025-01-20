package com.application.real_estate_app.feature_analytics.data.services

import com.application.real_estate_app.core.data_utils.data_models.AnalyticsEvent
import com.application.real_estate_app.core.interfaces.AnalyticsApiInterface
import com.application.real_estate_app.feature_analytics.data.apis.AnalyticsApi
import javax.inject.Inject

class ImplAnalyticsCore @Inject constructor(
    private val analyticsApi: AnalyticsApi
) : AnalyticsApiInterface {
    override suspend fun logEvent(event: AnalyticsEvent, onFailure: (Exception) -> Unit): Boolean {
        return analyticsApi.logEvent(event, onFailure)
    }
}