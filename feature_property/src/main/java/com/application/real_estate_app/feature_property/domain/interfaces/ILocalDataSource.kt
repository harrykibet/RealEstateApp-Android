package com.application.real_estate_app.feature_property.domain.interfaces

import com.application.real_estate_app.feature_property.data.entities.PropertyDraftEntity

interface ILocalDataSource {
    suspend fun saveDraft(draft: PropertyDraftEntity): Long
    suspend fun getAllDrafts(): List<PropertyDraftEntity>
    suspend fun getDraftById(draftId: Int): PropertyDraftEntity?
    suspend fun deleteDraft(draftId: Int)
    suspend fun clearAllDrafts()
}