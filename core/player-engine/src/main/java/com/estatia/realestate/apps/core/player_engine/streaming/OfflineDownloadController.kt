package com.estatia.realestate.apps.core.player_engine.streaming

import android.net.Uri
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class OfflineDownloadController @Inject constructor(
    private val downloadManager: DownloadManager
) {

    fun download(mediaId: String, uri: Uri) {
        val request = DownloadRequest.Builder(mediaId, uri).build()
        downloadManager.addDownload(request)
    }

    fun remove(mediaId: String) {
        downloadManager.removeDownload(mediaId)
    }

    fun observe(): Flow<List<Download>> = callbackFlow {
        // Create an explicit listener object
        val listener = object : DownloadManager.Listener {
            override fun onDownloadChanged(
                manager: DownloadManager,
                download: Download,
                finalException: Exception?
            ) {
                // Optional: handle single download change if needed
            }

            override fun onDownloadRemoved(
                manager: DownloadManager,
                download: Download
            ) {
                // Optional: handle removal if needed
            }

            // Add other overrides if your version of Media3 requires them
        }

        downloadManager.addListener(listener)
        awaitClose { downloadManager.removeListener(listener) }
    }
}