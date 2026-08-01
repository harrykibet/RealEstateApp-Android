package com.estatia.realestate.apps.core.common.interfaces

import android.content.Context
import android.net.Uri
import java.io.File

interface IMediaCompressor {
    fun compressImage(context: Context, imageUri: Uri, outputDir: File): File?
    fun compressVideo(context: Context, videoUri: Uri, outputDir: File, callback: (File?) -> Unit)
}
