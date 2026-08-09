package com.estatia.realestate.apps.core.database.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.database.entities.AnalyticsOutboxEntity

interface IAnalyticsLocalDataSource {
    suspend fun saveEvent(eventJson: String): AppResult<Unit>
    suspend fun getAllEvents(): AppResult<List<AnalyticsOutboxEntity>>
    suspend fun deleteEvent(event: AnalyticsOutboxEntity): AppResult<Unit>
}
