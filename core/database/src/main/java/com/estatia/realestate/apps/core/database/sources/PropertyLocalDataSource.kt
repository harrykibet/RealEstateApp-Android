package com.estatia.realestate.apps.core.database.sources

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.database.dao.PropertyCacheDao
import com.estatia.realestate.apps.core.database.dao.PropertyDraftDao
import com.estatia.realestate.apps.core.database.entities.PropertyCacheEntity
import com.estatia.realestate.apps.core.database.entities.PropertyDraftEntity
import com.estatia.realestate.apps.core.database.interfaces.ILocalDatabaseExecutor
import com.estatia.realestate.apps.core.database.interfaces.IPropertyLocalDataSource
import javax.inject.Inject

class PropertyLocalDataSource @Inject constructor(
    private val draftDao: PropertyDraftDao,
    private val cacheDao: PropertyCacheDao,
    private val databaseExecutor: ILocalDatabaseExecutor
) : IPropertyLocalDataSource {

    // ---------------- Drafts ----------------

    override suspend fun saveDraft(
        draft: PropertyDraftEntity
    ): AppResult<Long> =
        databaseExecutor.execute {
            draftDao.insertDraft(draft)
        }

    override suspend fun getAllDrafts()
            : AppResult<List<PropertyDraftEntity>> =
        databaseExecutor.execute {
            draftDao.getAllDrafts()
        }

    override suspend fun getDraftById(
        draftId: Long
    ): AppResult<PropertyDraftEntity> =
        databaseExecutor.execute {

            draftDao.getDraftById(draftId)
                ?: throw DatabaseException.NotFound
        }

    override suspend fun deleteDraft(
        draftId: Long
    ): AppResult<Unit> =
        databaseExecutor.execute {

            draftDao.deleteDraftById(draftId)
        }

    override suspend fun clearAllDrafts()
            : AppResult<Unit> =
        databaseExecutor.execute {

            draftDao.clearAllDrafts()
        }

    // ---------------- CACHE ----------------

    override suspend fun cacheProperties(
        properties: List<PropertyCacheEntity>
    ): AppResult<Unit> =
        databaseExecutor.execute {

            cacheDao.insertAll(properties)
        }

    override suspend fun getCachedProperties()
            : AppResult<List<PropertyCacheEntity>> =
        databaseExecutor.execute {

            cacheDao.getAll()
        }

    override suspend fun getCachedPropertyById(
        id: String
    ): AppResult<PropertyCacheEntity> =
        databaseExecutor.execute {

            cacheDao.getById(id)
                ?: throw DatabaseException.NotFound
        }

    override suspend fun clearCachedProperties()
            : AppResult<Unit> =
        databaseExecutor.execute {

            cacheDao.clearAll()
        }

    override suspend fun isCacheStale(
        maxAgeMillis: Long
    ): AppResult<Boolean> =
        databaseExecutor.execute {

            val latest = cacheDao.getLatestTimestamp()
                ?: return@execute true

            System.currentTimeMillis() - latest > maxAgeMillis
        }
}
