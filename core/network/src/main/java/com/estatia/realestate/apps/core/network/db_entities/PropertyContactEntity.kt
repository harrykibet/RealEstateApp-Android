package com.estatia.realestate.apps.core.network.db_entities
import com.estatia.realestate.apps.core.architecture.annotations.EntityModel

/**
 * Sensitive contact information for a property, stored in a gated subcollection.
 */
@EntityModel
data class PropertyContactEntity(
    val phone: String? = null,
    val email: String? = null
)
