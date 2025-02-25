package com.application.real_estate_app.core.utils.compression

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.application.real_estate_app.core.utils.system.FileUtils
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
        try {
            // Convert Uri to a real file path
            val inputPath = FileUtils.getPath(context, videoUri) ?: run {
                Log.e("FFmpeg", "Invalid video URI")
                callback(null)
                return
            }

            // Ensure output directory exists
            if (!outputDir.exists()) outputDir.mkdirs()

            // Define output file path
            val outputFile = File(outputDir, "compressed_video.mp4")

            // FFmpeg command for compression
            val ffmpegCommand = arrayOf(
                "-i", inputPath,         // Input file
                "-preset", "ultrafast",  // Encoding speed preset (slower = better compression)
                "-c:v", "libx264",       // H.264 video codec
                "-crf", "28",            // Constant Rate Factor (lower = better quality)
                "-c:a", "aac",           // Audio codec
                "-b:a", "128k",          // Audio bitrate
                "-strict", "experimental",
                outputFile.absolutePath  // Output file
            )

            // Execute FFmpeg command asynchronously
            FFmpeg.executeAsync(ffmpegCommand) { _, returnCode ->
                if (returnCode == Config.RETURN_CODE_SUCCESS) {
                    Log.d("FFmpeg", "Video compression completed successfully!")
                    callback(outputFile)
                } else {
                    Log.e("FFmpeg", "Video compression failed with return code: $returnCode")
                    callback(null)
                }
            }
        } catch (e: Exception) {
            Log.e("FFmpeg", "Error during compression: ${e.message}")
            callback(null)
        }
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
