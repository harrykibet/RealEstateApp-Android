package com.estatia.realestate.apps.core.domain.interfaces

import android.net.Uri
import com.estatia.realestate.apps.core.model.engagement.SafetyResult
import com.estatia.realestate.apps.core.model.engagement.SensitiveEntity

/**
 * Service for proactive content moderation and platform integrity checks.
 */
interface IContentSafetyService {
    /**
     * Validates text (comment, description) for toxicity, hate speech, or explicit content.
     */
    suspend fun validateText(text: String): SafetyResult

    /**
     * Scans text for sensitive data leakage (phone numbers, emails, addresses).
     * Used to prevent platform fee bypassing.
     */
    suspend fun detectSensitiveData(text: String): List<SensitiveEntity>

    /**
     * Scans an image for explicit or prohibited content.
     */
    suspend fun validateImage(imageUri: Uri): SafetyResult
}
