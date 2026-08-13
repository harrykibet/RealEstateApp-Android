package com.estatia.realestate.apps.core.intelligence

import android.net.Uri

/**
 * Service for analyzing property media to extract features and amenities.
 */
interface IMediaIntelligenceService {
    /**
     * Analyzes an image to detect amenities (e.g., pool, balcony, modern kitchen).
     * Returns a list of detected feature labels.
     */
    suspend fun extractAmenities(imageUri: Uri): List<String>

    /**
     * Checks if an image contains human faces for privacy protection.
     */
    suspend fun detectFaces(imageUri: Uri): Int

    /**
     * High-level quality score for the media (lighting, blur, etc.).
     */
    suspend fun getMediaQualityScore(imageUri: Uri): Float
}
