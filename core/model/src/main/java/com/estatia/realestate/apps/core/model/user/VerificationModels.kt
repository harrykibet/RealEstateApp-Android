package com.estatia.realestate.apps.core.model.user

/**
 * Data extracted from a government-issued ID document.
 */
data class IdDocumentResult(
    val name: String?,
    val idNumber: String?,
    val dateOfBirth: String?,
    val expiryDate: String?,
    val rawText: String,
    val confidence: Float
)

/**
 * Result of a face matching operation.
 */
data class FaceMatchResult(
    val confidence: Float,
    val isMatch: Boolean
)
