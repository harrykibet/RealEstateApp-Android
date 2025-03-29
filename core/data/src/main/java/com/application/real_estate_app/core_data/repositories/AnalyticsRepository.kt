package com.application.real_estate_app.core_data.repositories

import com.application.real_estate_app.core_data.interfaces.IAnalyticsRepository
import com.application.real_estate_app.core_model.AnalyticsEvent
import com.application.real_estate_app.core_network.interfaces.IAnalyticsRemoteDataSource
import javax.inject.Inject

class AnalyticsRepository @Inject constructor(
    private val remoteDataSource: IAnalyticsRemoteDataSource
) : IAnalyticsRepository {


    override suspend fun logEvent(
        message: String,
        eventType: String,
        customMetadata: Map<String, String>?,
        onFailure: (Exception) -> Unit
    ): Boolean {
        return remoteDataSource.logEvent(message, eventType, customMetadata, onFailure)
    }

    override suspend fun logEvent(event: AnalyticsEvent, onFailure: (Exception) -> Unit): Boolean {
        return remoteDataSource.logEvent(event, onFailure)
    }

    override suspend fun getEventsForUser(userId: String, onFailure: (Exception) -> Unit): List<AnalyticsEvent> {
        return remoteDataSource.getEventsForUser(userId, onFailure)
    }

    override suspend fun getEventById(eventId: String, onFailure: (Exception) -> Unit): AnalyticsEvent? {
        return remoteDataSource.getEventById(eventId, onFailure)
    }

    override suspend fun generateEventId(): String {
       return remoteDataSource.generateEventId()
    }
}
