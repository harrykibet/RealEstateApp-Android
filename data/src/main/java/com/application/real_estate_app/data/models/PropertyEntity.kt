package com.application.real_estate_app.data.models

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class PropertyEntity(
    var id: String? = null,
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    var imageUrl: List<String> = emptyList(),
    var videoUrl: List<String> = emptyList(),
    val video: Boolean = false,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    @ServerTimestamp val createdAt: Date? = null,
    val ownerId: String? = null,
    val ownerName: String? = null,
    val contactPhone: String? = null,
    val contactEmail: String? = null,
    val county: String? = null,
    val active: Boolean = true,
    val viewsCount: Int = 0,
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
        fun fromDocumentSnapshot(snapshot: DocumentSnapshot): PropertyEntity {
            return snapshot.toObject(PropertyEntity::class.java) ?: PropertyEntity()
        }
    }
}