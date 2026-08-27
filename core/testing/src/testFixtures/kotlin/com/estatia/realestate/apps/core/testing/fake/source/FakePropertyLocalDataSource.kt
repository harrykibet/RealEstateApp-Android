package com.estatia.realestate.apps.core.testing.fake.source

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.database.entities.CommentCacheEntity
import com.estatia.realestate.apps.core.database.entities.PropertyCacheEntity
import com.estatia.realestate.apps.core.database.entities.PropertyDraftEntity
import com.estatia.realestate.apps.core.database.interfaces.IPropertyLocalDataSource
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory fake implementation of [IPropertyLocalDataSource].
 */
class FakePropertyLocalDataSource : IPropertyLocalDataSource {

    private val cache = ConcurrentHashMap<String, PropertyCacheEntity>()
    private val drafts = ConcurrentHashMap<Long, PropertyDraftEntity>()
    private val comments = ConcurrentHashMap<String, MutableList<CommentCacheEntity>>()

    override suspend fun cacheProperties(properties: List<PropertyCacheEntity>): AppResult<Unit> {
        properties.forEach { cache[it.id] = it }
        return AppResult.Success(Unit)
    }

    override suspend fun getCachedProperties(): AppResult<List<PropertyCacheEntity>> {
        return AppResult.Success(cache.values.toList())
    }

    override suspend fun getCachedPropertiesByIds(propertyIds: List<String>): AppResult<List<PropertyCacheEntity>> {
        return AppResult.Success(propertyIds.mapNotNull { cache[it] })
    }

    override suspend fun getCachedPropertyById(id: String): AppResult<PropertyCacheEntity> {
        return cache[id]?.let { AppResult.Success(it) } ?: AppResult.Error(DatabaseException.NotFound)
    }

    override suspend fun clearCachedProperties(): AppResult<Unit> {
        cache.clear()
        return AppResult.Success(Unit)
    }

    override suspend fun isCacheStale(maxAgeMs: Long): AppResult<Boolean> {
        return AppResult.Success(false)
    }

    override suspend fun incrementLikes(propertyId: String): AppResult<Unit> {
        cache[propertyId]?.let {
            cache[propertyId] = it.copy(likesCount = it.likesCount + 1)
        }
        return AppResult.Success(Unit)
    }

    override suspend fun decrementLikes(propertyId: String): AppResult<Unit> {
        cache[propertyId]?.let {
            cache[propertyId] = it.copy(likesCount = (it.likesCount - 1).coerceAtLeast(0))
        }
        return AppResult.Success(Unit)
    }

    override suspend fun incrementViews(propertyId: String): AppResult<Unit> {
        cache[propertyId]?.let {
            cache[propertyId] = it.copy(viewsCount = it.viewsCount + 1)
        }
        return AppResult.Success(Unit)
    }

    override suspend fun incrementShares(propertyId: String): AppResult<Unit> {
        cache[propertyId]?.let {
            cache[propertyId] = it.copy(sharesCount = it.sharesCount + 1)
        }
        return AppResult.Success(Unit)
    }

    override suspend fun incrementComments(id: String): AppResult<Unit> {
        cache[id]?.let {
            cache[id] = it.copy(commentsCount = it.commentsCount + 1)
        }
        return AppResult.Success(Unit)
    }

    override suspend fun cacheComments(comments: List<CommentCacheEntity>): AppResult<Unit> {
        comments.forEach { comment ->
            val list = this.comments.getOrPut(comment.propertyId) { mutableListOf() }
            list.removeIf { it.id == comment.id }
            list.add(comment)
        }
        return AppResult.Success(Unit)
    }

    override suspend fun getCachedComments(propertyId: String): AppResult<List<CommentCacheEntity>> {
        return AppResult.Success(comments[propertyId] ?: emptyList())
    }

    override suspend fun clearCachedComments(propertyId: String): AppResult<Unit> {
        comments.remove(propertyId)
        return AppResult.Success(Unit)
    }

    override suspend fun saveDraft(draft: PropertyDraftEntity): AppResult<Long> {
        val id = if (draft.id == 0L) drafts.size.toLong() + 1 else draft.id
        drafts[id] = draft.copy(id = id)
        return AppResult.Success(id)
    }

    override suspend fun getAllDrafts(): AppResult<List<PropertyDraftEntity>> {
        return AppResult.Success(drafts.values.toList())
    }

    override suspend fun getDraftById(draftId: Long): AppResult<PropertyDraftEntity> {
        return drafts[draftId]?.let { AppResult.Success(it) } ?: AppResult.Error(DatabaseException.NotFound)
    }

    override suspend fun deleteDraft(draftId: Long): AppResult<Unit> {
        drafts.remove(draftId)
        return AppResult.Success(Unit)
    }

    override suspend fun clearAllDrafts(): AppResult<Unit> {
        drafts.clear()
        return AppResult.Success(Unit)
    }
}
