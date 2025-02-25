package com.application.real_estate_app.core.utils.system

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log

object FileUtils {
    fun getPath(context: Context, uri: Uri): String? {
        var cursor: Cursor? = null
        return try {
            val projection = arrayOf(MediaStore.Video.Media.DATA)
            cursor = context.contentResolver.query(uri, projection, null, null, null)
            cursor?.let {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                it.moveToFirst()
                it.getString(columnIndex)
            }
        } catch (e: Exception) {
            Log.e("FileUtils", "Failed to get file path: ${e.message}")
            null
        } finally {
            cursor?.close()
        }
    }
}
