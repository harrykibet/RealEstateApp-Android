package com.application.real_estate_app.core.interfaces

import com.application.real_estate_app.core.data_utils.data_models.AnalyticsEvent

interface AnalyticsApiInterface {
    suspend fun logEvent(event: AnalyticsEvent, onFailure: (Exception) -> Unit): Boolean
}