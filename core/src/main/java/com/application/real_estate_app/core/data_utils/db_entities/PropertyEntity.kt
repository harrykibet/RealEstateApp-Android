package com.application.real_estate_app.core.data_utils.db_entities

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class PropertyEntity(
    var id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val price: Double? = null,
    var imageUrl: List<String> = emptyList(),
    var videoUrl: List<String> = emptyList(),
    val video: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null,
    @ServerTimestamp val createdAt: Date? = null,
    val ownerId: String? = null,
    val ownerName: String? = null,
    val contactPhone: String? = null,
    val contactEmail: String? = null,
    val county: String? = null,
    val active: Boolean = true,
    val viewsCount: Int? = null,
    val propertyType: String? = null,
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val areaSize: Double? = null,
    val amenities: List<String>? = null,
    val features: String? = null,
    val depositAmount: Double? = null,
    val address: String? = null,
    val availableFrom: String? = null,
    val leaseTerms: String? = null,
    val available: Boolean = true
) {
    companion object {
        @Suppress("unused")
        fun fromDocumentSnapshot(snapshot: DocumentSnapshot): PropertyEntity {
            return snapshot.toObject(PropertyEntity::class.java) ?: PropertyEntity()
        }
    }
}