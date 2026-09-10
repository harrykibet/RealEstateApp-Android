package com.estatia.realestate.apps.core.domain.common

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.model.common.MediaReference
import com.estatia.realestate.apps.core.model.engagement.SafetyResult
import com.estatia.realestate.apps.core.model.engagement.SensitiveEntity
import com.estatia.realestate.apps.core.architecture.annotations.Contract

/**
 * Service for proactive content moderation and platform integrity checks.
 */
@Contract
interface IContentSafetyService {
    /**
     * Validates text (comment, description) for toxicity, hate speech, or explicit content.
     */
    suspend fun validateText(text: String): AppResult<SafetyResult>

    /**
     * Scans text for sensitive data leakage (phone numbers, emails, addresses).
     * Used to prevent platform fee bypassing.
     */
    suspend fun detectSensitiveData(text: String): AppResult<List<SensitiveEntity>>

    /**
     * Scans an image for explicit or prohibited content.
     */
    suspend fun validateImage(imageUri: MediaReference): AppResult<SafetyResult>

    /**
     * Scans a video for explicit or prohibited content by analyzing keyframes.
     */
    suspend fun validateVideo(videoUri: MediaReference): AppResult<SafetyResult>
}
