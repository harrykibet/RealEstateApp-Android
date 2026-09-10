package com.estatia.realestate.apps.core.model.intelligence
import com.estatia.realestate.apps.core.architecture.annotations.DomainModel

/**
 * Data extracted from a government-issued ID document.
 */
@DomainModel
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
@DomainModel
data class FaceMatchResult(
    val confidence: Float,
    val isMatch: Boolean
)
