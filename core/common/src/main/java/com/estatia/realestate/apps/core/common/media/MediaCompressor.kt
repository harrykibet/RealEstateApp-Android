package com.estatia.realestate.apps.core.common.media

import android.content.Context
import android.graphics.Bitmap.CompressFormat.*
import android.net.Uri
import com.estatia.realestate.apps.core.common.interfaces.IMediaCompressor
import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.common.system.FileUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class MediaCompressor @Inject constructor(
    private val logger: ILogger
) : IMediaCompressor {

    /**
     * Compresses an image while preserving quality.
     * Supports multiple formats including JPEG, PNG, WebP
     */
    override fun compressImage(context: Context, imageUri: Uri, outputDir: File): File? {
        return try {
            val file = FileUtils.getFileFromUri(context, imageUri) ?: return null

            val mediaFormat = MediaFileUtils.getMediaFormat(file) ?: run {
                logger.e(message = "Unsupported image format: ${file.extension}")
                return null
            }

            if (!MediaFileUtils.isImage(file)) {
                logger.e(message = "Not an image file: ${file.name}")
                return null
            }

            val format = when (mediaFormat) {
                MediaFormat.JPEG -> JPEG
                MediaFormat.PNG -> PNG
                MediaFormat.WEBP -> WEBP
                else -> JPEG
            }

            val bitmap = Glide.with(context)
                .asBitmap()
                .load(imageUri)
                .apply(RequestOptions().override(1080, 1080).diskCacheStrategy(DiskCacheStrategy.NONE))
                .submit()
                .get()

            val compressedFile = File(outputDir, "compressed_image_${System.currentTimeMillis()}.${mediaFormat.extension}")
            FileOutputStream(compressedFile).use { fos ->
                bitmap.compress(format, 80, fos)
            }

            logger.d(message = "Image compression successful: ${compressedFile.absolutePath}")
            compressedFile
        } catch (e: Exception) {
            logger.e(message = "Image compression failed: ${e.message}", throwable = e)
            null
        }
    }

    /**
     * Compresses a video file using FFmpeg.
     * Uses libx264 codec with optimized CRF settings for best size-quality tradeoff.
     */
    override fun compressVideo(context: Context, videoUri: Uri, outputDir: File, callback: (File?) -> Unit) {
        val file = FileUtils.getFileFromUri(context, videoUri) ?: run {
            logger.e(message = "Invalid video URI")
            callback(null)
            return
        }

        if (!MediaFileUtils.isVideo(file)) {
            logger.e(message = "Not a valid video file: ${file.name}")
            callback(null)
            return
        }

        // Ensure the output directory exists
        if (!outputDir.exists()) outputDir.mkdirs()

       // TODO("Fix FFmpeg discontinued support for Android.")
    }
}
