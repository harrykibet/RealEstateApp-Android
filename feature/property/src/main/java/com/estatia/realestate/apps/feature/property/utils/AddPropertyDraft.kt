package com.estatia.realestate.apps.feature.property.utils

import com.estatia.realestate.apps.core.model.common.MediaReference

data class AddPropertyDraft(
    val title: String = "",
    val description: String? = null,

    val price: Double? = null,
    val depositAmount: Double? = null,

    val county: String? = null,
    val propertyType: String? = null,

    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val areaSize: Double? = null,

    val amenities: Set<String> = emptySet(),

    val features: String? = null,

    val address: String? = null,

    val availableFrom: String? = null,
    val leaseTerms: String? = null,

    val latitude: Double? = null,
    val longitude: Double? = null,

    val contactEmail: String? = null,
    val contactPhone: String? = null,

    val images: List<MediaReference> = emptyList(),
    val videos: List<MediaReference> = emptyList()
)
