package com.application.real_estate_app.feature_service.data.db_entities

import com.application.real_estate_app.feature_service.data.utils.ServiceType
import com.application.real_estate_app.core.domain.models.Location
import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date

data class ServiceProviderEntity(
    val id: String? = null,
    val name: String? = null,
    val serviceType: ServiceType = ServiceType.UNKNOWN, // ENUM for categorizing services
    val description: String? = null, // Brief overview of the service
    val contactPhone: String? = null,
    val contactEmail: String? = null,
    val websiteUrl: String? = null,
    val location: Location? = null, // Geolocation of the service provider
    val serviceAreas: List<String> = emptyList(), // Counties or regions served
    val ratings: Float? = null, // Average rating
    val reviewsCount: Int = 0, // Number of reviews
    val verified: Boolean = false, // Whether the provider is verified
    val images: List<String> = emptyList(), // Photos of their services or offices
    val createdAt: Date? = null // Timestamp when the provider was added
) {
    companion object {
        @Suppress("unused")
        fun fromDocumentSnapshot(snapshot: DocumentSnapshot): ServiceProviderEntity {
            return snapshot.toObject(ServiceProviderEntity::class.java) ?: ServiceProviderEntity()
        }
    }
}
