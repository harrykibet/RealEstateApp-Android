package com.application.real_estate_app.core.utils.media

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.application.real_estate_app.core.domain.interfaces.IMediaCompressor
import com.application.real_estate_app.core.domain.interfaces.LoggerInterface
import com.application.real_estate_app.core.utils.system.FileUtils
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class MediaCompressor @Inject constructor(
    private val logger: LoggerInterface
) : IMediaCompressor {

    /**
     * Compresses an image while preserving quality.
     * Supports multiple formats including JPEG, PNG, WebP
     */
    override fun compressImage(context: Context, imageUri: Uri, outputDir: File): File? {
        return try {
            val file = FileUtils.getFileFromUri(context, imageUri) ?: return null

            val mediaFormat = MediaFileUtils.getMediaFormat(file) ?: run {
                logger.e("Unsupported image format: ${file.extension}")
                return null
            }

            if (!MediaFileUtils.isImage(file)) {
                logger.e("Not an image file: ${file.name}")
                return null
            }

            val format = when (mediaFormat) {
                MediaFormat.JPEG -> Bitmap.CompressFormat.JPEG
                MediaFormat.PNG -> Bitmap.CompressFormat.PNG
                MediaFormat.WEBP -> Bitmap.CompressFormat.WEBP
                else -> Bitmap.CompressFormat.JPEG
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

            logger.d("Image compression successful: ${compressedFile.absolutePath}")
            compressedFile
        } catch (e: Exception) {
            logger.e("Image compression failed: ${e.message}")
            null
        }
    }

    /**
     * Compresses a video file using FFmpeg.
     * Uses libx264 codec with optimized CRF settings for best size-quality tradeoff.
     */
    override fun compressVideo(context: Context, videoUri: Uri, outputDir: File, callback: (File?) -> Unit) {
        val file = FileUtils.getFileFromUri(context, videoUri) ?: run {
            logger.e("Invalid video URI")
            callback(null)
            return
        }

        if (!MediaFileUtils.isVideo(file)) {
            logger.e("Not a valid video file: ${file.name}")
            callback(null)
            return
        }

        // Ensure the output directory exists
        if (!outputDir.exists()) outputDir.mkdirs()

        val outputFile = File(outputDir, "compressed_video_${System.currentTimeMillis()}.mp4")

        val ffmpegCommand = "-i ${file.absolutePath} -preset veryfast -c:v libx264 -crf 28 -c:a aac -b:a 128k ${outputFile.absolutePath}"

        FFmpegKit.executeAsync(ffmpegCommand) { session ->
            val returnCode: ReturnCode? = session.returnCode

            when {
                ReturnCode.isSuccess(returnCode) -> {
                    logger.d("FFmpegKit: Video compression successful!")
                    callback(outputFile)
                }
                ReturnCode.isCancel(returnCode) -> {
                    logger.e("FFmpegKit: Compression canceled")
                    callback(null)
                }
                else -> {
                    logger.e("FFmpegKit: Compression failed: ${session.failStackTrace}")
                    callback(null)
                }
            }
        }
    }
}
