package com.estatia.realestate.apps.core.network.sources.aws

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.model.analytics.AnalyticsEvent
import com.estatia.realestate.apps.core.network.interfaces.IAnalyticsRemoteDataSource
import javax.inject.Inject

/**
 * AWS implementation of [IAnalyticsRemoteDataSource] (Skeleton).
 * This will use AWS Pinpoint for analytics in the future.
 */
internal class AwsAnalyticsRemoteDataSource @Inject constructor() : IAnalyticsRemoteDataSource {
    override suspend fun logEvent(event: AnalyticsEvent): AppResult<Unit> = AppResult.Success(Unit)
    override suspend fun getEventsForUser(userId: String): AppResult<List<AnalyticsEvent>> = AppResult.Success(emptyList())
    override suspend fun getEventById(eventId: String): AppResult<AnalyticsEvent?> = AppResult.Success(null)
    override fun generateEventId(): String = ""
    override suspend fun logEvent(message: String, eventType: String, customMetadata: Map<String, String>?): AppResult<Unit> = AppResult.Success(Unit)
}
