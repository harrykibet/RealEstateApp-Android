package com.estatia.realestate.apps.core.data.interfaces

import android.net.Uri
import androidx.lifecycle.LiveData
import com.estatia.realestate.apps.core.database.entities.PropertyDraftEntity
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel

interface IPropertyRepository {
    // LiveData to monitor the upload status
    val uploadStatus: LiveData<Boolean>
    val uploadError: LiveData<String?>

    suspend fun getDraftById(draftId: Int): PropertyDraftEntity?
    suspend fun saveDraft(draft: PropertyDraftEntity): Long
    suspend fun getAllDrafts(): List<PropertyDraftEntity>
    suspend fun deleteDraft(draftId: Int)
    suspend fun clearAllDrafts()

    suspend fun uploadProperty(
        property: PropertyDomainModel,
        imageUris: List<Uri>,
        videoUris: List<Uri>,
        onFailure: (Exception) -> Unit
    ): Boolean?

    // Update Property
    suspend fun updateProperty(propertyId: String, updates: Map<String, Any>, onFailure: (Exception) -> Unit): Boolean

    // Delete Property
    suspend fun deleteProperty(propertyId: String, onFailure: (Exception) -> Unit): Boolean

    // Get Property by Id
    suspend fun getPropertyById(propertyId: String, onFailure: (Exception) -> Unit): PropertyDomainModel?

    suspend fun fetchLikedProperties(
        userId: String,
        onFailure: (Exception) -> Unit
    ): List<PropertyDomainModel>?

    suspend fun likeProperty(
        userId: String,
        propertyId: String,
        onFailure: (Exception) -> Unit
    ): Boolean

    suspend fun unlikeProperty(
        userId: String,
        propertyId: String,
        onFailure: (Exception) -> Unit
    ): Boolean

    // Fetch Properties Paginated
    suspend fun fetchPropertiesPaginated(
        lastVisible: String?,
        pageSize: Int,
        onFailure: (Exception) -> Unit
    ): Pair<List<PropertyDomainModel>, String?>

    suspend fun searchProperties(
        query: String,
        limit: Int,
        onFailure: (Exception) -> Unit
    ): List<PropertyDomainModel>
}