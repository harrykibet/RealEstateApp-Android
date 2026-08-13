package com.estatia.realestate.apps.core.intelligence

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [IMediaIntelligenceService] using Google ML Kit.
 */
@Singleton
class MlKitMediaIntelligenceService @Inject constructor(
    @ApplicationContext private val context: Context
) : IMediaIntelligenceService {

    private val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
    private val faceDetector = FaceDetection.getClient()

    override suspend fun extractAmenities(imageUri: Uri): List<String> {
        val image = InputImage.fromFilePath(context, imageUri)
        val labels = labeler.process(image).await()
        
        // Filter and map labels to Estatia amenities
        return labels
            .filter { it.confidence > 0.7f }
            .map { it.text.lowercase() }
            .filter { isRelevantAmenity(it) }
    }

    override suspend fun detectFaces(imageUri: Uri): Int {
        val image = InputImage.fromFilePath(context, imageUri)
        val faces = faceDetector.process(image).await()
        return faces.size
    }

    override suspend fun getMediaQualityScore(imageUri: Uri): Float {
        // Simple heuristic: higher confidence labels often mean clearer images
        val image = InputImage.fromFilePath(context, imageUri)
        val labels = labeler.process(image).await()
        return labels.firstOrNull()?.confidence ?: 0.5f
    }

    private fun isRelevantAmenity(label: String): Boolean {
        val knownAmenities = setOf(
            "swimming pool", "balcony", "kitchen", "bathroom", "bedroom",
            "garden", "garage", "furniture", "living room"
        )
        return knownAmenities.any { label.contains(it) }
    }
}
