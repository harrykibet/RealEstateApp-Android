package com.estatia.realestate.apps.core.database.interfaces

import com.estatia.realestate.apps.core.database.entities.PropertyCacheEntity
import com.estatia.realestate.apps.core.database.entities.PropertyDraftEntity

interface IPropertyLocalDataSource {

    // Drafts (feature-specific)
    suspend fun saveDraft(draft: PropertyDraftEntity): Long
    suspend fun getAllDrafts(): List<PropertyDraftEntity>
    suspend fun getDraftById(draftId: Int): PropertyDraftEntity?
    suspend fun deleteDraft(draftId: Int)
    suspend fun clearAllDrafts()

    // -----------------------------
    // CACHE LAYER (NEW)
    // -----------------------------

    suspend fun cacheProperties(properties: List<PropertyCacheEntity>)

    suspend fun getCachedProperties(): List<PropertyCacheEntity>

    suspend fun getCachedPropertyById(id: String): PropertyCacheEntity?

    suspend fun clearCachedProperties()

    suspend fun isCacheStale(maxAgeMillis: Long): Boolean
}