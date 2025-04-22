package com.application.real_estate_app.core_database.interfaces

import com.application.real_estate_app.core_database.entities.PropertyDraftEntity

interface IPropertyLocalDataSource {
    suspend fun saveDraft(draft: PropertyDraftEntity): Long
    suspend fun getAllDrafts(): List<PropertyDraftEntity>
    suspend fun getDraftById(draftId: Int): PropertyDraftEntity?
    suspend fun deleteDraft(draftId: Int)
    suspend fun clearAllDrafts()
}