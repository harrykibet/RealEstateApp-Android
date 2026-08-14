package com.estatia.realestate.apps.core.model.property

data class PropertyDomainModel(
    val id: PropertyId,
    val title: String,

    val description: String?,
    val price: Money?,

    val imageUrls: List<String>,
    val directVideoUrls: List<String>,
    val hlsUrls: List<String>,
    val videosAvailable: Boolean,

    val coordinates: Coordinates?,

    val createdAt: Long?,

    val ownerId: String?,
    val ownerName: String?,

    val contact: ContactInfo,

    val county: String?,

    val active: Boolean,

    val viewsCount: Int,
    val likesCount: Int,
    val commentsCount: Int,
    val sharesCount: Int,

    val matchScore: Float,

    val propertyType: String?,

    val bedrooms: Int?,
    val bathrooms: Int?,
    val areaSize: Double?,

    val amenities: List<String>,

    val features: String?,
    val depositAmount: Money?,

    val address: String?,
    val availableFrom: String?,
    val leaseTerms: String?,

    val available: Boolean
)
