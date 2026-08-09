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
    private val downloadManager: DownloadManager,
    private val cacheKeyFactory: ICacheKeyFactory
) {

    fun download(mediaId: String, uri: Uri) {
        val stableKey = cacheKeyFactory.resolveStableKey(uri, mediaId)
        val request = DownloadRequest.Builder(stableKey, uri)
            .setCustomCacheKey(stableKey)
            .build()
        downloadManager.addDownload(request)
    }

    fun remove(mediaId: String) {
        downloadManager.removeDownload(mediaId)
    }


    fun observe(): Flow<List<Download>> = callbackFlow {
        trySend(downloadManager.currentDownloads)

        val listener = object : DownloadManager.Listener {
            override fun onDownloadChanged(
                manager: DownloadManager,
                download: Download,
                finalException: Exception?
            ) {
                trySend(manager.currentDownloads)
            }

            override fun onDownloadRemoved(
                manager: DownloadManager,
                download: Download
            ) {
                trySend(manager.currentDownloads)
            }
        }

        downloadManager.addListener(listener)
        awaitClose { downloadManager.removeListener(listener) }
    }
}
