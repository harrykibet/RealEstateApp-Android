package com.estatia.realestate.apps.core.database.sources

import com.estatia.realestate.apps.core.database.dao.PropertyDraftDao
import com.estatia.realestate.apps.core.database.interfaces.IPropertyLocalDataSource
import com.estatia.realestate.apps.core.database.entities.PropertyDraftEntity
import javax.inject.Inject

class PropertyLocalDataSource @Inject constructor(
    private val draftDao: PropertyDraftDao
) : IPropertyLocalDataSource {

    // Save or update a property draft
    override suspend fun saveDraft(draft: PropertyDraftEntity): Long {
        return draftDao.insertDraft(draft)
    }

    // Retrieve all property drafts
    override suspend fun getAllDrafts(): List<PropertyDraftEntity> {
        return draftDao.getAllDrafts()
    }

    // Retrieve a specific draft by its ID
    override suspend fun getDraftById(draftId: Int): PropertyDraftEntity? {
        return draftDao.getDraftById(draftId)
    }

    // Delete a specific draft by its ID
    override suspend fun deleteDraft(draftId: Int) {
        draftDao.deleteDraftById(draftId)
    }

    // Clear all drafts
    override suspend fun clearAllDrafts() {
        draftDao.clearAllDrafts()
    }
}
