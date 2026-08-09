package com.estatia.realestate.apps.core.data.repositories

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.model.analytics.AnalyticsEvent
import com.estatia.realestate.apps.core.domain.interfaces.IAnalyticsTracker
import com.estatia.realestate.apps.core.network.interfaces.IAnalyticsRemoteDataSource
import com.estatia.realestate.apps.core.database.interfaces.IAnalyticsLocalDataSource
import com.estatia.realestate.apps.core.data.worker.AnalyticsSyncWorker
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


internal class AnalyticsTracker @Inject constructor(
    private val remoteDataSource: IAnalyticsRemoteDataSource,
    private val localDataSource: IAnalyticsLocalDataSource,
    private val logger: ILogger,
    private val gson: Gson,
    @ApplicationContext private val context: Context
) : IAnalyticsTracker {

    private val workManager by lazy { WorkManager.getInstance(context) }

    override suspend fun logEvent(
        message: String,
        eventType: String,
        customMetadata: Map<String, String>?
    ) {
        when (val result = remoteDataSource.logEvent(message, eventType, customMetadata)) {
            is AppResult.Success -> Unit
            is AppResult.Error -> {
                logger.e(message = "Analytics logging failed, saving to outbox", throwable = result.exception)
                // For simple message-based events, we could either discard or wrap them. 
                // Let's focus on structured events for now or convert this to a structured event.
            }
        }
    }

    override suspend fun logEvent(event: AnalyticsEvent) {
        when (val result = remoteDataSource.logEvent(event)) {
            is AppResult.Success -> Unit
            is AppResult.Error -> {
                logger.e(message = "Analytics logging failed, saving to outbox", throwable = result.exception)
                localDataSource.saveEvent(gson.toJson(event))
                scheduleSync()
            }
        }
    }

    override suspend fun syncEvents(): AppResult<Unit> {
        val pendingEvents = localDataSource.getAllEvents()
        if (pendingEvents is AppResult.Success) {
            pendingEvents.data.forEach { entity ->
                val event = gson.fromJson(entity.eventJson, AnalyticsEvent::class.java)
                when (remoteDataSource.logEvent(event)) {
                    is AppResult.Success -> localDataSource.deleteEvent(entity)
                    is AppResult.Error -> return@forEach // Stop if network still fails
                }
            }
        }
        return AppResult.Success(Unit)
    }

    private fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<AnalyticsSyncWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueue(request)
    }

    override fun generateEventId(): String {
        return remoteDataSource.generateEventId()
    }
}
