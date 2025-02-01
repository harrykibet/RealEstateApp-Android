package com.application.real_estate_app.feature_mediaplayer.streaming

import android.telecom.TelecomManager.PRIORITY_NORMAL
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.util.concurrent.PriorityBlockingQueue
import javax.inject.Inject
import javax.inject.Singleton

// Priority-based segment downloads
@Singleton
@UnstableApi
class ChunkDownloader @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    private val downloadQueue = PriorityBlockingQueue<ChunkRequest>()

    fun enqueueDownload(request: ChunkRequest) {
        downloadQueue.put(request)
        startDownloadWorker()
    }

    private fun startDownloadWorker() = CoroutineScope(Dispatchers.IO).launch {
        while (!downloadQueue.isEmpty()) {
            val chunk = downloadQueue.take()
            okHttpClient.newCall(chunk.toRequest()).execute()
        }
    }

    data class ChunkRequest(
        val url: String,
        val priority: Int = PRIORITY_NORMAL
    )
}