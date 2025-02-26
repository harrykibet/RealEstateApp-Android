package com.application.real_estate_app.core.utils.system

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import com.application.real_estate_app.core.utils.media.MediaFileUtils
import com.application.real_estate_app.core.utils.media.MediaFileUtils.getMediaFilePathFromContentUri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileUtils {

    private const val TAG = "FileUtils"

    /**
     * Retrieves the file path from a given [Uri].
     */
    fun getPath(context: Context, uri: Uri): String? {
        return when {
            DocumentsContract.isDocumentUri(context, uri) -> getDocumentFilePath(context, uri)
            "content".equals(uri.scheme, ignoreCase = true) -> getMediaFilePath(context, uri)
            "file".equals(uri.scheme, ignoreCase = true) -> uri.path
            else -> null
        }
    }

    /**
     * Retrieves the absolute file path for a media file (Image, Video, Audio).
     */
    private fun getMediaFilePath(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        return context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            if (cursor.moveToFirst()) cursor.getString(columnIndex) else null
        }
    }

    /**
     * Retrieves the file path for documents, including those stored in external storage.
     */
    private fun getDocumentFilePath(context: Context, uri: Uri): String? {
        val docId = DocumentsContract.getDocumentId(uri)
        return when {
            uri.authority == "com.android.externalstorage.documents" -> {
                val parts = docId.split(":")
                if (parts.size == 2) {
                    val type = parts[0]
                    val relativePath = parts[1]
                    if ("primary".equals(type, ignoreCase = true)) {
                        "${Environment.getExternalStorageDirectory()}/$relativePath"
                    } else null
                } else null
            }
            uri.authority == "com.android.providers.downloads.documents" -> {
                val contentUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI
                } else {
                    Uri.parse("content://downloads/public_downloads")
                }
                getMediaFilePathFromContentUri(context, contentUri, docId)
            }
            uri.authority == "com.android.providers.media.documents" -> {
                val contentUri = when {
                    docId.startsWith("image:") -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    docId.startsWith("video:") -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    docId.startsWith("audio:") -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    else -> return null
                }
                getMediaFilePathFromContentUri(context, contentUri, docId.split(":")[1])
            }
            else -> null
        }
    }

    /**
     * Retrieves a file from a given URI by copying it to the cache directory.
     */
    fun getFileFromUri(context: Context, uri: Uri): File? {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        if (inputStream == null) {
            Log.e(TAG, "Failed to open input stream for URI: $uri")
            return null
        }

        val outputFile = File(context.cacheDir, "temp_${System.currentTimeMillis()}")
        return try {
            FileOutputStream(outputFile).use { output ->
                inputStream.copyTo(output)
            }
            outputFile
        } catch (e: Exception) {
            Log.e(TAG, "Error saving file from URI: ${e.message}")
            null
        }
    }

    /**
     * Deletes a file safely.
     */
    fun deleteFile(file: File?): Boolean {
        return file?.let {
            if (it.exists()) it.delete() else false
        } ?: false
    }

    /**
     * Checks if a file exists.
     */
    fun doesFileExist(path: String?): Boolean {
        return path?.let { File(it).exists() } ?: false
    }
}
