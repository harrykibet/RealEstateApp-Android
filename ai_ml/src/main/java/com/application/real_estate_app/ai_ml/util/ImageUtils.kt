package com.application.real_estate_app.machine_learning.util

import android.graphics.Bitmap
import android.media.Image
import com.google.mlkit.vision.common.InputImage

object ImageUtils {
    fun fromBitmap(bitmap: Bitmap): InputImage {
        return InputImage.fromBitmap(bitmap, 0)
    }

    fun fromMediaImage(image: Image, rotation: Int): InputImage {
        return InputImage.fromMediaImage(image, rotation)
    }
}
