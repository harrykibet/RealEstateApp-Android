package com.application.real_estate_app.core.domain.models

import java.util.Date

data class Property(
    val id: String?,
    val title: String?,
    val description: String?,
    val price: Double?,
    val imageUrl: List<String>,
    val videoUrl: List<String>,
    val video: Boolean,
    val latitude: Double?,
    val longitude: Double?,
    val createdAt: Date?,
    val ownerId: String?,
    val ownerName: String?,
    val contactPhone: String?,
    val contactEmail: String?,
    val county: String?,
    val active: Boolean,
    val viewsCount: Int?,
    val propertyType: String?,
    val bedrooms: Int?,
    val bathrooms: Int?,
    val areaSize: Double?,
    val amenities: List<String>?,
    val features: String?,
    val depositAmount: Double?,
    val address: String?,
    val availableFrom: String?,
    val leaseTerms: String?,
    val available: Boolean
)
