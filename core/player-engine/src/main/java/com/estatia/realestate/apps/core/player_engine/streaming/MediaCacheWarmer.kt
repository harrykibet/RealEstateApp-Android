package com.estatia.realestate.apps.core.player_engine.streaming

import android.net.Uri
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSourceInputStream
import androidx.media3.datasource.DataSpec
import com.estatia.realestate.apps.core.player_engine.di.EngineScope
import com.estatia.realestate.apps.core.player_engine.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class MediaCacheWarmer @Inject constructor(
    private val playbackDataSourceFactory: DataSource.Factory,
    private val networkUtils: INetworkUtils,
    @param:EngineScope private val scope: CoroutineScope,
    @param:IODispatcher private val ioDispatcher: CoroutineDispatcher
) : AutoCloseable {


    private val requests = MutableSharedFlow<WarmRequest>(
        extraBufferCapacity = 16,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    @Volatile
    private var visibleJob: Job? = null

    @Volatile
    private var nextJob: Job? = null

    @Volatile
    private var buffering = false

    init {
        scope.launch {
            requests.collect { request ->
                when (request.priority) {
                    WarmPriority.VISIBLE -> handleVisible(request)
                    WarmPriority.NEXT -> handleNext(request)
                }
            }
        }
    }

    fun prefetch(uri: Uri, priority: WarmPriority) {
        requests.tryEmit(WarmRequest(uri, priority))
    }

    fun onBufferingStarted() {
        scope.launch {
            buffering = true
            nextJob?.cancel()
            nextJob = null
        }
    }

    fun onBufferingEnded() {
        buffering = false
    }

    private fun handleVisible(request: WarmRequest) {
        visibleJob?.cancel()

        visibleJob = scope.launch {
            prefetchInternal(
                uri = request.uri,
                maxBytes = adaptivePrefetchSize(
                    visible = true
                )
            )
        }
    }

    private fun handleNext(request: WarmRequest) {
        if (buffering) return

        nextJob?.cancel()

        nextJob = scope.launch {
            prefetchInternal(
                uri = request.uri,
                maxBytes = adaptivePrefetchSize(
                    visible = false
                )
            )
        }
    }

    private fun adaptivePrefetchSize(visible: Boolean): Long {
        return when {
            networkUtils.isLowLatencyNetwork() -> {
                if (visible) 5L * 1024 * 1024 else 3L * 1024 * 1024
            }
            networkUtils.isNetworkMetered() -> {
                if (visible) 2L * 1024 * 1024 else 1L * 1024 * 1024
            }
            else -> {
                if (visible) 3L * 1024 * 1024 else 2L * 1024 * 1024
            }
        }
    }

    private suspend fun prefetchInternal(
        uri: Uri,
        maxBytes: Long
    ) = withContext(ioDispatcher) {

        val dataSpec = DataSpec(uri)
        val dataSource = playbackDataSourceFactory.createDataSource()

        DataSourceInputStream(dataSource, dataSpec).use { input ->
            val buffer = ByteArray(32 * 1024)
            var total = 0L

            while (total < maxBytes && currentCoroutineContext().isActive) {
                val read = input.read(buffer)
                if (read == -1) break
                total += read
            }
        }
    }

    suspend fun cancelAllJobs() {
        visibleJob?.cancelAndJoin()
        nextJob?.cancelAndJoin()
        visibleJob = null
        nextJob = null
    }

    override fun close() {
        visibleJob?.cancel()
        nextJob?.cancel()
    }
}