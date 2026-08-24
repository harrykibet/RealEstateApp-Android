package com.estatia.realestate.apps.core.database.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.database.entities.AnalyticsOutboxEntity

/**
 * Interface for the local analytics outbox.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Manage the persistence of telemetry events until upload.
 * - Concurrency: Implementations must be thread-safe.
 */
interface IAnalyticsLocalDataSource {
    suspend fun saveEvent(eventJson: String): AppResult<Unit>
    suspend fun getAllEvents(): AppResult<List<AnalyticsOutboxEntity>>
    suspend fun deleteEvent(event: AnalyticsOutboxEntity): AppResult<Unit>
}
