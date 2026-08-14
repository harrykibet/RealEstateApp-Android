package com.estatia.realestate.apps.core.intelligence

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.estatia.realestate.apps.core.domain.interfaces.IContentSafetyService
import com.estatia.realestate.apps.core.model.engagement.SafetyResult
import com.estatia.realestate.apps.core.model.engagement.SensitiveEntity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [IContentSafetyService] using ML Kit and heuristic patterns.
 */
@Singleton
class MlKitContentSafetyService @Inject constructor(
    @ApplicationContext private val context: Context
) : IContentSafetyService {

    private val labeler by lazy { ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS) }

    override suspend fun validateText(text: String): SafetyResult {
        // ML Kit doesn't have a direct on-device "toxicity" model.
        // We use a high-performance heuristic pattern match for common abusive terms.
        val toxicTerms = setOf("abusive_term1", "abusive_term2") // Placeholder
        val lowerText = text.lowercase()
        
        if (toxicTerms.any { lowerText.contains(it) }) {
            return SafetyResult.Flagged("Content contains abusive language", 0.95f)
        }
        
        return SafetyResult.Safe
    }

    override suspend fun detectSensitiveData(text: String): List<SensitiveEntity> {
        // Pattern match for phone numbers and emails (common bypass methods)
        val entities = mutableListOf<SensitiveEntity>()
        
        val phoneRegex = Regex("\\+?\\d{10,12}")
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")

        phoneRegex.findAll(text).forEach { 
            entities.add(SensitiveEntity("PHONE", it.value, it.range.first, it.range.last))
        }
        
        emailRegex.findAll(text).forEach {
            entities.add(SensitiveEntity("EMAIL", it.value, it.range.first, it.range.last))
        }

        return entities
    }

    override suspend fun validateImage(imageUri: Uri): SafetyResult {
        val image = InputImage.fromFilePath(context, imageUri)
        return runLabelAnalysis(image)
    }

    override suspend fun validateVideo(videoUri: Uri): SafetyResult = withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, videoUri)
            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val durationMs = durationStr?.toLong() ?: 0L

            // Analyze 5 keyframes evenly spread across the video
            val framesToAnalyze = 5
            for (i in 0 until framesToAnalyze) {
                val timeUs = (durationMs * 1000 / framesToAnalyze) * i
                val frame = retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                
                if (frame != null) {
                    val image = InputImage.fromBitmap(frame, 0)
                    val result = runLabelAnalysis(image)
                    if (result is SafetyResult.Flagged) {
                        return@withContext result
                    }
                }
            }
            SafetyResult.Safe
        } catch (e: Exception) {
            SafetyResult.Safe // Fallback to safe if extraction fails
        } finally {
            retriever.release()
        }
    }

    private suspend fun runLabelAnalysis(image: InputImage): SafetyResult {
        val labels = labeler.process(image).await()
        
        // Check for unsafe labels (ML Kit default models include some safety labels)
        val unsafeLabels = setOf("violence", "weapon", "nude", "explicit", "sexual")
        
        val hit = labels.find { label ->
            unsafeLabels.any { label.text.lowercase().contains(it) } && label.confidence > 0.6f
        }

        return if (hit != null) {
            SafetyResult.Flagged("Content contains prohibited visual elements: ${hit.text}", hit.confidence)
        } else {
            SafetyResult.Safe
        }
    }
}
