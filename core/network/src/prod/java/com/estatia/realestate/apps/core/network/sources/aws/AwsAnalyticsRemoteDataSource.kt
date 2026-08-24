package com.estatia.realestate.apps.core.network.sources.aws

import com.amplifyframework.analytics.AnalyticsEvent
import com.amplifyframework.core.Amplify
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.model.analytics.AnalyticsEvent as DomainEvent
import com.estatia.realestate.apps.core.network.interfaces.IAnalyticsRemoteDataSource
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import javax.inject.Inject

/**
 * AWS implementation of [IAnalyticsRemoteDataSource].
 * Uses Amazon Pinpoint via Amplify.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Direct ingestion of events into AWS Pinpoint.
 * - Concurrency: Thread-safe (SDK internal).
 * - Observability: Tracks recording events success/failure.
 */
internal class AwsAnalyticsRemoteDataSource @Inject constructor(
    private val metricsTracker: IMetricsTracker
) : IAnalyticsRemoteDataSource {

    override suspend fun logEvent(event: DomainEvent): AppResult<Unit> {
        val amplifyEvent = AnalyticsEvent.builder()
            .name(event.eventType)
            .addProperty("userId", event.userId)
            .addProperty("eventId", event.eventId)
            .apply {
                event.metadata.forEach { (key, value) ->
                    addProperty("meta_$key", value)
                }
                event.deviceInfo.let { device ->
                    addProperty("device_os", device.os)
                    addProperty("app_version", device.appVersion)
                }
            }
            .build()

        Amplify.Analytics.recordEvent(amplifyEvent)
        metricsTracker.incrementCounter("network.analytics.record")
        return AppResult.Success(Unit)
    }

    override suspend fun logEvent(message: String, eventType: String, customMetadata: Map<String, String>?): AppResult<Unit> {
        val event = AnalyticsEvent.builder()
            .name(eventType)
            .addProperty("message", message)
            .apply {
                customMetadata?.forEach { (key, value) ->
                    addProperty("meta_$key", value)
                }
            }
            .build()
        
        Amplify.Analytics.recordEvent(event)
        metricsTracker.incrementCounter("network.analytics.record")
        return AppResult.Success(Unit)
    }

    @Deprecated("Raw event querying is no longer supported on the client.")
    override suspend fun getEventsForUser(userId: String): AppResult<List<DomainEvent>> = AppResult.Success(emptyList())

    @Deprecated("Raw event querying is no longer supported on the client.")
    override suspend fun getEventById(eventId: String): AppResult<DomainEvent?> = AppResult.Success(null)

    override fun generateEventId(): String = java.util.UUID.randomUUID().toString()
}
