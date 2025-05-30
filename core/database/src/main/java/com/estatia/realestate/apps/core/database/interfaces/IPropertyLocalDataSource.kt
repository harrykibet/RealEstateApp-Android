package com.estatia.realestate.apps.core.database.interfaces

import com.estatia.realestate.apps.core.database.entities.PropertyDraftEntity

interface IPropertyLocalDataSource {
    suspend fun saveDraft(draft: PropertyDraftEntity): Long
    suspend fun getAllDrafts(): List<PropertyDraftEntity>
    suspend fun getDraftById(draftId: Int): PropertyDraftEntity?
    suspend fun deleteDraft(draftId: Int)
    suspend fun clearAllDrafts()
}