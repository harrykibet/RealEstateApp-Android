package com.application.real_estate_app.feature_property.domain.interfaces

import android.net.Uri
import androidx.lifecycle.LiveData
import com.application.real_estate_app.core.data_utils.models.Property

interface IFeaturePropertyRepo {
    // LiveData to monitor the upload status
    val uploadStatus: LiveData<Boolean>
    val uploadError: LiveData<String?>

    // Property Upload
    suspend fun uploadProperty(
        property: Property,
        imageUris: List<Uri>,
        videoUris: List<Uri>
    ): Boolean

    // Update Property
    suspend fun updateProperty(propertyId: String, updates: Map<String, Any>): Boolean

    // Delete Property
    suspend fun deleteProperty(propertyId: String): Boolean

    // Get Property by Id
    suspend fun getPropertyById(propertyId: String): Property?
}