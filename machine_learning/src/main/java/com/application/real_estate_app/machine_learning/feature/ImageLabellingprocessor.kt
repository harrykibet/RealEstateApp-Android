package com.application.real_estate_app.machine_learning.feature

import com.application.real_estate_app.machine_learning.core.MLKitProcessor
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class ImageLabelingProcessor : MLKitProcessor() {
    private val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

    override suspend fun processImage(image: InputImage): Result {
        // Implementation here
    }

    override fun stop() {
        labeler.close()
    }
}
