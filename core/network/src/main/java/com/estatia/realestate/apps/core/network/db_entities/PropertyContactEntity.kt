package com.estatia.realestate.apps.core.network.db_entities

/**
 * Sensitive contact information for a property, stored in a gated subcollection.
 */
data class PropertyContactEntity(
    val phone: String? = null,
    val email: String? = null
)
