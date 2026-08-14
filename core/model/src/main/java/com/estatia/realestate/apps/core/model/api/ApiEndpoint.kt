package com.estatia.realestate.apps.core.model.api

/**
 * Represents a regional API endpoint for multi-region failover.
 */
data class ApiEndpoint(
    val name: String,
    val baseUrl: String,
    val priority: Int = 0 // 0 is primary
)
