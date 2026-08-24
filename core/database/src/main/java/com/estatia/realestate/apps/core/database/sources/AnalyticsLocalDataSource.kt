package com.estatia.realestate.apps.core.database.sources

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.database.dao.AnalyticsOutboxDao
import com.estatia.realestate.apps.core.database.entities.AnalyticsOutboxEntity
import com.estatia.realestate.apps.core.database.interfaces.IAnalyticsLocalDataSource
import com.estatia.realestate.apps.core.database.interfaces.ILocalDatabaseExecutor
import javax.inject.Inject

/**
 * Local data source for analytics events using an outbox pattern.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Manage the secure persistence of analytics events until synchronization.
 * - Concurrency: Thread-safe; delegates to [databaseExecutor].
 * - Resilience: Surfaces domain-specific failures via [databaseExecutor].
 */
internal class AnalyticsLocalDataSource @Inject constructor(
    private val dao: AnalyticsOutboxDao,
    private val databaseExecutor: ILocalDatabaseExecutor
) : IAnalyticsLocalDataSource {

    override suspend fun saveEvent(eventJson: String): AppResult<Unit> =
        databaseExecutor.execute {
            dao.insert(AnalyticsOutboxEntity(eventJson = eventJson))
        }

    override suspend fun getAllEvents(): AppResult<List<AnalyticsOutboxEntity>> =
        databaseExecutor.execute {
            dao.getAll()
        }

    override suspend fun deleteEvent(event: AnalyticsOutboxEntity): AppResult<Unit> =
        databaseExecutor.execute {
            dao.delete(event)
        }
}
