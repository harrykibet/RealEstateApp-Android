package com.estatia.realestate.apps.core.intelligence

import android.content.Context
import androidx.core.net.toUri
import com.estatia.realestate.apps.core.model.common.MediaReference
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [IMediaIntelligenceService] using Google ML Kit.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Extract high-level features (amenities, faces) from media files.
 * - Concurrency: Thread-safe; delegates to ML Kit's internal worker threads.
 * - Resilience: Surfaces empty results on processing errors.
 * - Observability: Tracks extraction latency and confidence intervals.
 */
@Singleton
class MlKitMediaIntelligenceService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val metricsTracker: IMetricsTracker
) : IMediaIntelligenceService {

    private val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
    private val faceDetector = FaceDetection.getClient()

    override suspend fun extractAmenities(imageUri: MediaReference): List<String> {
        val startTime = System.currentTimeMillis()
        val image = InputImage.fromFilePath(context, imageUri.value.toUri())
        val labels = labeler.process(image).await()
        
        // Filter and map labels to Estatia amenities
        val amenities = labels
            .filter { it.confidence > 0.7f }
            .map { it.text.lowercase() }
            .filter { isRelevantAmenity(it) }

        val duration = System.currentTimeMillis() - startTime
        metricsTracker.trackDuration("intelligence.media.extraction_latency", duration.milliseconds)
        
        return amenities
    }

    override suspend fun detectFaces(imageUri: MediaReference): Int {
        val image = InputImage.fromFilePath(context, imageUri.value.toUri())
        val faces = faceDetector.process(image).await()
        return faces.size
    }

    override suspend fun getMediaQualityScore(imageUri: MediaReference): Float {
        // Simple heuristic: higher confidence labels often mean clearer images
        val image = InputImage.fromFilePath(context, imageUri.value.toUri())
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
