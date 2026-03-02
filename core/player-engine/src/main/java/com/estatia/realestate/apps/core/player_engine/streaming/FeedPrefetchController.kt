package com.estatia.realestate.apps.core.player_engine.streaming

import android.net.Uri
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSourceInputStream
import androidx.media3.datasource.DataSpec
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@UnstableApi
class FeedPrefetchController(
    private val playbackDataSourceFactory: DataSource.Factory,
    dispatcher: CoroutineDispatcher,
    private val ioDispatcher: CoroutineDispatcher
) : AutoCloseable {

    private val scope = CoroutineScope(
        SupervisorJob() + dispatcher.limitedParallelism(1)
    )

    @Volatile
    private var queue = createChannel()

    private var currentJob: Job? = null

    init {
        startConsumer()
    }

    private fun createChannel() =
        Channel<Uri>(
            capacity = 32,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

    private fun startConsumer() {
        scope.launch {
            for (uri in queue) {
                currentJob = launch {
                    prefetchInternal(uri)
                }
                currentJob?.join()
            }
        }
    }

    fun prefetch(uri: Uri) {
        queue.trySend(uri)
    }

    suspend fun cancelAll() {
        currentJob?.cancelAndJoin()

        // Replace queue entirely (clean slate)
        val oldQueue = queue
        queue = createChannel()
        oldQueue.close()

        startConsumer()
    }

    private suspend fun prefetchInternal(
        uri: Uri,
        maxBytes: Long = 3 * 1024 * 1024
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

    override fun close() {
        scope.cancel()
    }
}