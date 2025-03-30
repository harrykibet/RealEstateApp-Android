package com.application.real_estate_app.core_data.interfaces

import android.net.Uri
import androidx.lifecycle.LiveData
import com.application.real_estate_app.core_database.entities.PropertyDraftEntity
import com.application.real_estate_app.core_model.property.Property

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
        property: Property,
        imageUris: List<Uri>,
        videoUris: List<Uri>,
        onFailure: (Exception) -> Unit
    ): Boolean?

    // Update Property
    suspend fun updateProperty(propertyId: String, updates: Map<String, Any>, onFailure: (Exception) -> Unit): Boolean

    // Delete Property
    suspend fun deleteProperty(propertyId: String, onFailure: (Exception) -> Unit): Boolean

    // Get Property by Id
    suspend fun getPropertyById(propertyId: String, onFailure: (Exception) -> Unit): Property?

    suspend fun fetchLikedProperties(
        userId: String,
        onFailure: (Exception) -> Unit
    ): List<Property>?

    suspend fun toggleLikeProperty(
        userId: String,
        propertyId: String,
        onFailure: (Exception) -> Unit
    ): Boolean

    // Fetch Properties Paginated
    suspend fun fetchPropertiesPaginated(
        lastVisible: String?,
        pageSize: Int,
        onFailure: (Exception) -> Unit
    ): Pair<List<Property>, String?>

    suspend fun searchProperties(
        query: String,
        limit: Int,
        onFailure: (Exception) -> Unit
    ): List<Property>
}