package com.estatia.realestate.apps.core.model.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a regional API endpoint for multi-region failover.
 */
@Serializable
data class ApiEndpoint(
    val name: String,
    @SerialName("base_url")
    val baseUrl: String,
    val priority: Int = 0 // 0 is primary
)
