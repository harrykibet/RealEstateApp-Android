package com.application.real_estate_app.machine_learning.core

import com.google.mlkit.vision.common.InputImage

abstract class MLKitProcessor {
    abstract suspend fun processImage(image: InputImage): Result
    abstract fun stop()
}
