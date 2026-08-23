package com.estatia.realestate.apps.core.intelligence

import com.estatia.realestate.apps.core.model.common.MediaReference

/**
 * Service for analyzing property media to extract features and amenities.
 */
interface IMediaIntelligenceService {
    /**
     * Analyzes an image to detect amenities (e.g., pool, balcony, modern kitchen).
     * Returns a list of detected feature labels.
     */
    suspend fun extractAmenities(imageUri: MediaReference): List<String>

    /**
     * Checks if an image contains human faces for privacy protection.
     */
    suspend fun detectFaces(imageUri: MediaReference): Int

    /**
     * High-level quality score for the media (lighting, blur, etc.).
     */
    suspend fun getMediaQualityScore(imageUri: MediaReference): Float
}
