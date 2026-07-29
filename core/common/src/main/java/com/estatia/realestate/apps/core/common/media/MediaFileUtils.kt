package com.estatia.realestate.apps.core.common.media

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.net.toFile
import androidx.core.net.toUri
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest

/**
 * A utility class for handling media files, providing methods for determining file types,
 * extracting metadata, and working with URIs.
 */
object MediaFileUtils {

    private const val TAG = "MediaFileUtils"
    private const val EXTERNAL_FILES_URI = "external"

    // ────────────────────────────────────────────────────────────────────────
    // Media Type Detection
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Determines the media type of a file.
     */
    fun getMediaFormat(file: File): MediaFormat? {
        return MediaFormat.fromExtension(file.extension.lowercase())
    }

    /**
     * Determines if a file is an image, video, or audio.
     */
    fun isImage(file: File): Boolean = getMediaFormat(file) in MediaFormat.IMAGE_FORMATS
    fun isVideo(file: File): Boolean = getMediaFormat(file) in MediaFormat.VIDEO_FORMATS
    fun isAudio(file: File): Boolean = getMediaFormat(file) in MediaFormat.AUDIO_FORMATS

    /**
     * Determines if a URI is an image, video, or audio.
     */
    fun isImage(context: Context, uri: Uri): Boolean = getMimeType(context, uri)?.startsWith("image/") == true
    fun isVideo(context: Context, uri: Uri): Boolean = getMimeType(context, uri)?.startsWith("video/") == true
    fun isAudio(context: Context, uri: Uri): Boolean = getMimeType(context, uri)?.startsWith("audio/") == true

    // ────────────────────────────────────────────────────────────────────────
    // MIME Type and File Extension Utilities
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Retrieves the file extension from a file.
     */
    fun getFileExtension(file: File): String = file.extension.lowercase()

    /**
     * Retrieves the file extension from a URI.
     */
    fun getFileExtension(context: Context, uri: Uri): String? {
        return getMimeType(context, uri)?.let { MimeTypeMap.getSingleton().getExtensionFromMimeType(it) }
    }

    /**
     * Retrieves the MIME type of a file.
     */
    fun getMimeType(file: File): String? {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(getFileExtension(file))
    }

    /**
     * Retrieves the MIME type from a URI.
     */
    fun getMimeType(context: Context, uri: Uri): String? {
        return context.contentResolver.getType(uri)
    }

    // ────────────────────────────────────────────────────────────────────────
    // File Path Resolution
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Resolves the absolute file path from a URI.
     */
    fun getPath(context: Context, uri: Uri): String? {
        return when {
            DocumentsContract.isDocumentUri(context, uri) -> resolveDocumentUri(context, uri)
            uri.isMediaStoreUri() -> getMediaFilePathFromContentUri(context, uri, null)
            uri.isFileUri() -> uri.path
            else -> null
        }
    }

    /**
     * Resolves the file path for downloads on Android 9 (API 28) and below.
     */
    private fun getLegacyDownloadFilePath(context: Context, docId: String): Uri? {
        return try {
            val contentUri = ContentUris.withAppendedId(
                "content://downloads/public_downloads".toUri(),
                docId.toLong()
            )
            contentUri
        } catch (e: NumberFormatException) {
            Log.e("FileUtils", "Error parsing download document ID: $docId")
            null
        }
    }


    /**
     * Loads all media files (images and videos) from storage and adds them to the provided list.
     */
    fun loadMedia(context: Context, mediaList: MutableList<Uri>) {
        val projection = arrayOf(MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.MEDIA_TYPE)
        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} IN (?, ?)"
        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )

        val queryUri = MediaStore.Files.getContentUri(EXTERNAL_FILES_URI)

        context.contentResolver.query(queryUri, projection, selection, selectionArgs, null)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(queryUri, id)
                mediaList.add(contentUri)
            }
        } ?: Log.e(TAG, "Failed to load media from storage.")
    }


    private fun resolveDocumentUri(context: Context, uri: Uri): String? {
        val docId = DocumentsContract.getDocumentId(uri)
        return when {
            uri.isExternalStorageDocument() -> {
                val split = docId.split(":")
                if (split.size == 2) "${Environment.getExternalStorageDirectory()}/${split[1]}" else null
            }
            uri.isDownloadsDocument() -> {
                val contentUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI
                } else {
                    getLegacyDownloadFilePath(context, docId) // Handle legacy cases
                }

                return contentUri?.let { getMediaFilePathFromContentUri(context, it, docId) }
            }
            uri.isMediaDocument() -> {
                val split = docId.split(":")
                val type = split[0]
                val contentUri = when (type) {
                    "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    else -> null
                }
                contentUri?.let { getMediaFilePathFromContentUri(context, it, split[1]) }
            }
            else -> null
        }
    }

    private fun getAlternativeFilePath(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File(context.cacheDir, "temp_${System.currentTimeMillis()}")
            tempFile.outputStream().use { outputStream -> inputStream.copyTo(outputStream) }
            inputStream.close()
            tempFile.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Fallback method failed: ${e.message}")
            null
        }
    }


    fun getMediaFilePathFromContentUri(
        context: Context,
        contentUri: Uri,
        docId: String?
    ): String? {
        var cursor: Cursor? = null
        return try {
            val projection = arrayOf(MediaStore.MediaColumns.DATA)
            val selection = if (docId != null) "${MediaStore.MediaColumns._ID}=?" else null
            val selectionArgs = docId?.let { arrayOf(it) }

            cursor = context.contentResolver.query(contentUri, projection, selection, selectionArgs, null)

            cursor?.use {
                val columnIndex = it.getColumnIndex(MediaStore.MediaColumns.DATA)

                if (columnIndex != -1 && it.moveToFirst()) {
                    it.getString(columnIndex)
                } else {
                    Log.e(TAG, "Column 'DATA' not found, falling back to alternative method.")
                    getAlternativeFilePath(context, contentUri) // Fallback method
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving file path: ${e.message}")
            null
        } finally {
            cursor?.close()
        }
    }


    // ────────────────────────────────────────────────────────────────────────
    // File Size Utilities
    // ────────────────────────────────────────────────────────────────────────

    fun getFileSize(context: Context, uri: Uri): Long? {
        return try {
            val file = uri.toFile()
            file.length()
        } catch (e: IOException) {
            null
        }
    }

    fun isFileSizeExceedingLimit(fileSize: Long, maxSize: Long = 50 * 1024 * 1024): Boolean = fileSize > maxSize
    fun shouldCompress(fileSize: Long, threshold: Long = 10 * 1024 * 1024): Boolean = fileSize > threshold

    // ────────────────────────────────────────────────────────────────────────
    // File Validation & Integrity Checks
    // ────────────────────────────────────────────────────────────────────────

    fun isMediaFileValid(file: File): Boolean {
        return try {
            FileInputStream(file).use { it.read() >= 0 }
        } catch (e: IOException) {
            false
        }
    }

    fun getFileChecksum(file: File): String? {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val inputStream = FileInputStream(file)
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
            inputStream.close()
            val hashBytes = digest.digest()
            BigInteger(1, hashBytes).toString(16).padStart(64, '0')
        } catch (e: Exception) {
            null
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Extensions
    // ────────────────────────────────────────────────────────────────────────

    private fun Uri.isExternalStorageDocument() = authority == "com.android.externalstorage.documents"
    private fun Uri.isDownloadsDocument() = authority == "com.android.providers.downloads.documents"
    private fun Uri.isMediaDocument() = authority == "com.android.providers.media.documents"
    private fun Uri.isMediaStoreUri() = authority!!.startsWith("media")
    private fun Uri.isFileUri() = scheme == "file"
}
