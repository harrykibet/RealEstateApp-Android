package com.application.real_estate_app.machine_learning.feature

import com.application.real_estate_app.machine_learning.core.MLKitProcessor
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.tasks.await

class FaceDetectionProcessor : MLKitProcessor() {

    // Configure the detector options as needed
    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST) // Adjust to PERFORMANCE_MODE_ACCURATE if needed
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.1f) // Minimum size for detecting faces
            .enableTracking() // Enables face tracking
            .build()
    )

    override suspend fun processImage(image: InputImage): Result {
        return try {
            val faces = detector.process(image).await()
            Result(faces)
        } catch (e: Exception) {
            // Handle errors appropriately
            Result(emptyList<Face>(), metadata = null)
        }
    }

    override fun stop() {
        detector.close()
    }
}
