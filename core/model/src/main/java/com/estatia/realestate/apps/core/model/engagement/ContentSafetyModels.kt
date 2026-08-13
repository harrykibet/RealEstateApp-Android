package com.estatia.realestate.apps.core.model.engagement

/**
 * Result of a content safety check.
 */
sealed interface SafetyResult {
    /**
     * Content is clean and safe to submit.
     */
    data object Safe : SafetyResult

    /**
     * Content violates safety policies.
     * @param reason Human-readable reason for rejection.
     * @param confidence Model's confidence in the rejection.
     */
    data class Flagged(val reason: String, val confidence: Float) : SafetyResult
}

/**
 * Represents a sensitive entity detected in text (e.g. personal contact info).
 */
data class SensitiveEntity(
    val type: String,
    val value: String,
    val start: Int,
    val end: Int
)
