package com.application.real_estate_app.feature_property.domain.interfaces

import android.net.Uri
import androidx.lifecycle.LiveData
import com.application.real_estate_app.core.domain.models.Property
import com.application.real_estate_app.feature_property.data.entities.PropertyDraftEntity

interface IPropertyRepository {

    // Local Draft Operations
    suspend fun saveDraft(draft: PropertyDraftEntity): Long
    suspend fun getAllDrafts(): List<PropertyDraftEntity>
    suspend fun getDraftById(draftId: Int): PropertyDraftEntity?
    suspend fun deleteDraft(draftId: Int)
    suspend fun clearAllDrafts()

    // Remote Property Operations
    val uploadStatus: LiveData<Boolean>
    val uploadError: LiveData<String?>

    suspend fun uploadProperty(
        property: Property,
        imageUris: List<Uri>,
        videoUris: List<Uri>,
        onFailure: (Exception) -> Unit
    ): Boolean?

    suspend fun updateProperty(propertyId: String, updates: Map<String, Any>, onFailure: (Exception) -> Unit): Boolean

    suspend fun getPropertyById(propertyId: String, onFailure: (Exception) -> Unit): Property?

    suspend fun deleteProperty(propertyId: String, onFailure: (Exception) -> Unit): Boolean
}
