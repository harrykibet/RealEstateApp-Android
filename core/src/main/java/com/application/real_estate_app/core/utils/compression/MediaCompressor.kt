package com.application.real_estate_app.core.utils.compression

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import java.io.File
import java.io.FileOutputStream


object MediaCompressor {

    // Compression threshold (5MB)
    private const val SIZE_THRESHOLD = 5 * 1024 * 1024 // 5MB

    // Image compression function using Glide
    fun compressImage(context: Context, imageUri: Uri, outputDir: File): File? {
        return try {
            val bitmap = Glide.with(context)
                .asBitmap()
                .load(imageUri)
                .apply(RequestOptions().override(1080, 1080).encodeFormat(Bitmap.CompressFormat.JPEG).diskCacheStrategy(DiskCacheStrategy.NONE))
                .submit()
                .get()

            val compressedFile = File(outputDir, "compressed_image_${System.currentTimeMillis()}.jpg")
            val fos = FileOutputStream(compressedFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos)
            fos.close()
            compressedFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Video compression function
    fun compressVideo(context: Context, videoUri: Uri, outputDir: File, callback: (File?) -> Unit) {
        TODO("Video compression not implemented yet")
    }


    // Function to check if the file size exceeds the threshold
    fun shouldCompress(fileSize: Long): Boolean {
        return fileSize > SIZE_THRESHOLD
    }

    // Optional: Max size limit check (50MB)
    fun isFileSizeExceedingLimit(fileSize: Long, maxSize: Long = 50 * 1024 * 1024): Boolean {
        return fileSize > maxSize
    }
}
