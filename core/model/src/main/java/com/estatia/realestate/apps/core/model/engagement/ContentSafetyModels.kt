package com.estatia.realestate.apps.core.model.engagement
import com.estatia.realestate.apps.core.architecture.annotations.Data.DomainModel
import com.estatia.realestate.apps.core.architecture.annotations.Identity.Contract

/**
 * Result of a content safety check.
 */
@Contract
sealed interface SafetyResult {
    /**
     * Content is clean and safe to submit.
     */
@DomainModel
    data object Safe : SafetyResult

    /**
     * Content violates safety policies.
     * @param reason Human-readable reason for rejection.
     * @param confidence Model's confidence in the rejection.
     */
@DomainModel
    data class Flagged(val reason: String, val confidence: Float) : SafetyResult
}

/**
 * Represents a sensitive entity detected in text (e.g. personal contact info).
 */
@DomainModel
data class SensitiveEntity(
    val type: String,
    val value: String,
    val start: Int,
    val end: Int
)
