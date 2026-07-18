package com.estatia.realestate.apps.core.database.sources

import com.estatia.realestate.apps.core.database.dao.PropertyCacheDao
import com.estatia.realestate.apps.core.database.dao.PropertyDraftDao
import com.estatia.realestate.apps.core.database.entities.PropertyCacheEntity
import com.estatia.realestate.apps.core.database.entities.PropertyDraftEntity
import com.estatia.realestate.apps.core.database.interfaces.IPropertyLocalDataSource
import javax.inject.Inject

class PropertyLocalDataSource @Inject constructor(
    private val draftDao: PropertyDraftDao,
    private val cacheDao: PropertyCacheDao
) : IPropertyLocalDataSource {

    // ---------------- Drafts ----------------

    override suspend fun saveDraft(draft: PropertyDraftEntity): Long {
        return draftDao.insertDraft(draft)
    }

    override suspend fun getAllDrafts(): List<PropertyDraftEntity> {
        return draftDao.getAllDrafts()
    }

    override suspend fun getDraftById(draftId: Long): PropertyDraftEntity? {
        return draftDao.getDraftById(draftId)
    }

    override suspend fun deleteDraft(draftId: Long) {
        draftDao.deleteDraftById(draftId)
    }

    override suspend fun clearAllDrafts() {
        draftDao.clearAllDrafts()
    }

    // ---------------- CACHE ----------------

    override suspend fun cacheProperties(properties: List<PropertyCacheEntity>) {
        cacheDao.insertAll(properties)
    }

    override suspend fun getCachedProperties(): List<PropertyCacheEntity> {
        return cacheDao.getAll()
    }

    override suspend fun getCachedPropertyById(id: String): PropertyCacheEntity? {
        return cacheDao.getById(id)
    }

    override suspend fun clearCachedProperties() {
        cacheDao.clearAll()
    }

    override suspend fun isCacheStale(maxAgeMillis: Long): Boolean {
        val latest = cacheDao.getLatestTimestamp() ?: return true
        return (System.currentTimeMillis() - latest) > maxAgeMillis
    }
}