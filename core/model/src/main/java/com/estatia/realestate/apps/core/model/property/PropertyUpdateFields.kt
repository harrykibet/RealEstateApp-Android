package com.estatia.realestate.apps.core.model.property

import kotlinx.serialization.Serializable

/**
 * Represents an allowlisted set of fields that can be updated on a property listing.
 * This prevents arbitrary field updates and potential rule-bypass vectors.
 */
@Serializable
data class PropertyUpdateFields(
    val title: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val depositAmount: Double? = null,
    val county: String? = null,
    val propertyType: String? = null,
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val areaSize: Double? = null,
    val amenities: List<String>? = null,
    val features: String? = null,
    val address: String? = null,
    val availableFrom: String? = null,
    val leaseTerms: String? = null,
    val available: Boolean? = null,
    val active: Boolean? = null
)
