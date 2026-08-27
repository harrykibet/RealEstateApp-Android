package com.estatia.realestate.apps.core.database.sources

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.database.dao.CommentCacheDao
import com.estatia.realestate.apps.core.database.dao.PropertyCacheDao
import com.estatia.realestate.apps.core.database.dao.PropertyDraftDao
import com.estatia.realestate.apps.core.database.entities.CommentCacheEntity
import com.estatia.realestate.apps.core.database.entities.PropertyCacheEntity
import com.estatia.realestate.apps.core.database.entities.PropertyDraftEntity
import com.estatia.realestate.apps.core.database.interfaces.ILocalDatabaseExecutor
import com.estatia.realestate.apps.core.database.interfaces.IPropertyLocalDataSource
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import javax.inject.Inject

private const val MAX_PROPERTIES = 200
private const val TARGET_PROPERTIES = 160
private const val MAX_COMMENTS = 500
private const val TARGET_COMMENTS = 400

/**
 * Local data source for property-related data using Room.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Ownership: Source of truth for local drafts and first-page cache.
 * - Concurrency: Thread-safe; delegates to [databaseExecutor].
 * - Resilience: Implements size-bounded LRU-style trimming for property and comment caches.
 * - Performance: Offloads all I/O to background threads.
 * - Observability: Tracks cache trim operations.
 */
internal class PropertyLocalDataSource @Inject constructor(
    private val draftDao: PropertyDraftDao,
    private val cacheDao: PropertyCacheDao,
    private val commentDao: CommentCacheDao,
    private val metricsTracker: IMetricsTracker,
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
            if (cacheDao.count() > MAX_PROPERTIES) {
                metricsTracker.incrementCounter("database.cache.property_trim")
                cacheDao.trim(TARGET_PROPERTIES)
            }
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

    override suspend fun getCachedPropertiesByIds(
        ids: List<String>
    ): AppResult<List<PropertyCacheEntity>> =
        databaseExecutor.execute {
            cacheDao.getByIds(ids)
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

    override suspend fun incrementLikes(id: String): AppResult<Unit> =
        databaseExecutor.execute {
            cacheDao.incrementLikes(id)
        }

    override suspend fun decrementLikes(id: String): AppResult<Unit> =
        databaseExecutor.execute {
            cacheDao.decrementLikes(id)
        }

    override suspend fun incrementViews(id: String): AppResult<Unit> =
        databaseExecutor.execute {
            cacheDao.incrementViews(id)
        }

    override suspend fun incrementShares(id: String): AppResult<Unit> =
        databaseExecutor.execute {
            cacheDao.incrementShares(id)
        }

    override suspend fun incrementComments(id: String): AppResult<Unit> =
        databaseExecutor.execute {
            cacheDao.incrementComments(id)
        }

    // ---------------- COMMENTS CACHE ----------------

    override suspend fun cacheComments(
        comments: List<CommentCacheEntity>
    ): AppResult<Unit> =
        databaseExecutor.execute {
            commentDao.insertAll(comments)
            if (commentDao.count() > MAX_COMMENTS) {
                metricsTracker.incrementCounter("database.cache.comment_trim")
                commentDao.trim(TARGET_COMMENTS)
            }
        }

    override suspend fun getCachedComments(
        propertyId: String
    ): AppResult<List<CommentCacheEntity>> =
        databaseExecutor.execute {
            commentDao.getForProperty(propertyId)
        }

    override suspend fun clearCachedComments(
        propertyId: String
    ): AppResult<Unit> =
        databaseExecutor.execute {
            commentDao.clearForProperty(propertyId)
        }
}
