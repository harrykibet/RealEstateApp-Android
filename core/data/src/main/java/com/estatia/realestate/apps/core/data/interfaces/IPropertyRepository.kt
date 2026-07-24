package com.estatia.realestate.apps.core.data.interfaces

import android.net.Uri
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.model.property.PropertyCursor
import com.estatia.realestate.apps.core.model.property.PropertyDraftDomainModel
import com.estatia.realestate.apps.core.model.property.PropertyPage

interface IPropertyRepository {
    suspend fun saveDraft(draft: PropertyDraftDomainModel): AppResult<Long>
    suspend fun getAllDrafts(): AppResult<List<PropertyDraftDomainModel>>
    suspend fun getDraftById(draftId: Long): AppResult<PropertyDraftDomainModel?>
    suspend fun deleteDraft(draftId: Long): AppResult<Unit>
    suspend fun clearAllDrafts(): AppResult<Unit>

    suspend fun uploadProperty(
        property: PropertyDomainModel,
        imageUris: List<Uri>,
        videoUris: List<Uri>
    ): AppResult<String>

    // Update Property
    suspend fun updateProperty(
        propertyId: String,
        updates: Map<String, Any>
    ): AppResult<Unit>

    // Delete Property
    suspend fun deleteProperty(
        propertyId: String
    ): AppResult<Unit>

      suspend fun getPropertyById(
        propertyId: String
    ): AppResult<PropertyDomainModel>

    suspend fun fetchLikedProperties(
        userId: String
    ): AppResult<List<PropertyDomainModel>>

    suspend fun likeProperty(
        userId: String,
        propertyId: String
    ): AppResult<Unit>

    suspend fun unlikeProperty(
        userId: String,
        propertyId: String
    ): AppResult<Unit>

    suspend fun searchProperties(
        query: String,
        limit: Int
    ): AppResult<List<PropertyDomainModel>>

    suspend fun fetchPropertiesPaginated(
        cursor: PropertyCursor?,
        pageSize: Int
    ): AppResult<PropertyPage>

}