package com.estatia.realestate.apps.core.localization.model
import com.estatia.realestate.apps.core.architecture.annotations.Logic.Utility

/**
 * Represents a geographical region for localization purposes.
 */
@Utility
data class Region(
    val code: String,
    val name: String,
    val currencyCode: String,
    val usesMetric: Boolean = true
)

val SupportedRegions = listOf(
    Region("KE", "Kenya", "KES"),
    Region("US", "United States", "USD", usesMetric = false),
    Region("FR", "France", "EUR"),
    Region("ES", "Spain", "EUR")
)
