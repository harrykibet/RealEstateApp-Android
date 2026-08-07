package com.estatia.realestate.apps.core.database.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.database.entities.CommentCacheEntity
import com.estatia.realestate.apps.core.database.entities.PropertyCacheEntity
import com.estatia.realestate.apps.core.database.entities.PropertyDraftEntity

interface IPropertyLocalDataSource {

    // -----------------------------
    // Drafts
    // -----------------------------

    suspend fun saveDraft(
        draft: PropertyDraftEntity
    ): AppResult<Long>

    suspend fun getAllDrafts(): AppResult<List<PropertyDraftEntity>>

    suspend fun getDraftById(
        draftId: Long
    ): AppResult<PropertyDraftEntity>

    suspend fun deleteDraft(
        draftId: Long
    ): AppResult<Unit>

    suspend fun clearAllDrafts(): AppResult<Unit>

    // -----------------------------
    // Cache
    // -----------------------------

    suspend fun cacheProperties(
        properties: List<PropertyCacheEntity>
    ): AppResult<Unit>

    suspend fun getCachedProperties(): AppResult<List<PropertyCacheEntity>>

    suspend fun getCachedPropertyById(
        id: String
    ): AppResult<PropertyCacheEntity>

    suspend fun getCachedPropertiesByIds(
        ids: List<String>
    ): AppResult<List<PropertyCacheEntity>>

    suspend fun clearCachedProperties(): AppResult<Unit>

    suspend fun isCacheStale(
        maxAgeMillis: Long
    ): AppResult<Boolean>

    // -----------------------------
    // Comments Cache
    // -----------------------------

    suspend fun cacheComments(
        comments: List<CommentCacheEntity>
    ): AppResult<Unit>

    suspend fun getCachedComments(
        propertyId: String
    ): AppResult<List<CommentCacheEntity>>

    suspend fun clearCachedComments(
        propertyId: String
    ): AppResult<Unit>
}
