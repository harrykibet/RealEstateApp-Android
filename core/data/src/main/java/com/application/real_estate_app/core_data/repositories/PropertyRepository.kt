package com.application.real_estate_app.core_data.repositories

import android.net.Uri
import androidx.lifecycle.LiveData
import com.application.real_estate_app.core_data.interfaces.IPropertyRepository
import com.application.real_estate_app.core_database.entities.PropertyDraftEntity
import com.application.real_estate_app.core_database.interfaces.IPropertyLocalDataSource
import com.application.real_estate_app.core_model.Property
import com.application.real_estate_app.core_network.interfaces.IPropertyRemoteDatasource
import javax.inject.Inject

class PropertyRepository @Inject constructor(
    private val localDataSource: IPropertyLocalDataSource,
    private val remoteDataSource: IPropertyRemoteDatasource
) : IPropertyRepository {

    // Local Draft Operations
    override suspend fun saveDraft(draft: PropertyDraftEntity): Long {
        return localDataSource.saveDraft(draft)
    }

    override suspend fun getAllDrafts(): List<PropertyDraftEntity> {
        return localDataSource.getAllDrafts()
    }

    override suspend fun getDraftById(draftId: Int): PropertyDraftEntity? {
        return localDataSource.getDraftById(draftId)
    }

    override suspend fun deleteDraft(draftId: Int) {
        localDataSource.deleteDraft(draftId)
    }

    override suspend fun clearAllDrafts() {
        localDataSource.clearAllDrafts()
    }

    // Remote Property Operations
    override val uploadStatus: LiveData<Boolean>
        get() = remoteDataSource.uploadStatus

    override val uploadError: LiveData<String?>
        get() = remoteDataSource.uploadError

    override suspend fun uploadProperty(
        property: Property,
        imageUris: List<Uri>,
        videoUris: List<Uri>,
        onFailure: (Exception) -> Unit
    ): Boolean? {
        return remoteDataSource.uploadProperty(property, imageUris, videoUris, onFailure)
    }

    override suspend fun updateProperty(
        propertyId: String,
        updates: Map<String, Any>,
        onFailure: (Exception) -> Unit
    ): Boolean {
        return remoteDataSource.updateProperty(propertyId, updates, onFailure)
    }

    override suspend fun getPropertyById(
        propertyId: String,
        onFailure: (Exception) -> Unit
    ): Property? {
        return remoteDataSource.getPropertyById(propertyId, onFailure)
    }

    override suspend fun fetchLikedProperties(
        userId: String,
        onFailure: (Exception) -> Unit
    ): List<Property>? {
        return remoteDataSource.fetchLikedProperties(userId, onFailure)
    }

    override suspend fun toggleLikeProperty(
        userId: String,
        propertyId: String,
        onFailure: (Exception) -> Unit
    ): Boolean {
        return remoteDataSource.toggleLikeProperty(userId, propertyId, onFailure)
    }

    override suspend fun fetchPropertiesPaginated(
        lastVisible: String?,
        pageSize: Int,
        onFailure: (Exception) -> Unit
    ): Pair<List<Property>, String?> {
        return remoteDataSource.fetchPropertiesPaginated(lastVisible, pageSize, onFailure)
    }

    override suspend fun searchProperties(
        query: String,
        limit: Int,
        onFailure: (Exception) -> Unit
    ): List<Property> {
        return remoteDataSource.searchProperties(query, limit, onFailure)
    }

    override suspend fun deleteProperty(
        propertyId: String,
        onFailure: (Exception) -> Unit
    ): Boolean {
        return remoteDataSource.deleteProperty(propertyId, onFailure)
    }
}
