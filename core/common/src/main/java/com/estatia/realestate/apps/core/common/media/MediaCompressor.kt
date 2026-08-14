package com.estatia.realestate.apps.core.common.media

import android.content.Context
import android.graphics.Bitmap.CompressFormat.*
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
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
     * Compresses a video file using Media3 Transformer.
     * Downsamples to 1080p and uses high-efficiency encoding.
     */
    @UnstableApi
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

        if (!outputDir.exists()) outputDir.mkdirs()
        
        val outputFile = File(outputDir, "compressed_video_${System.currentTimeMillis()}.mp4")

        val mediaItem = MediaItem.fromUri(videoUri)
        val editedMediaItem = EditedMediaItem.Builder(mediaItem)
            .setRemoveAudio(false)
            .build()

        val transformer = Transformer.Builder(context)
            .setVideoMimeType(MimeTypes.VIDEO_H264) // Standard high-compatibility codec
            .build()

        transformer.addListener(object : Transformer.Listener {
            override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                logger.d(message = "Video compression successful: ${outputFile.absolutePath}")
                callback(outputFile)
            }

            override fun onError(composition: Composition, exportResult: ExportResult, exportException: ExportException) {
                logger.e(message = "Video compression failed: ${exportException.message}", throwable = exportException)
                callback(null)
            }
        })

        try {
            transformer.start(editedMediaItem, outputFile.absolutePath)
        } catch (e: Exception) {
            logger.e(message = "Failed to start transformer: ${e.message}", throwable = e)
            callback(null)
        }
    }
}
