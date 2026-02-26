package com.estatia.realestate.apps.core.player_engine.streaming

import android.net.Uri
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSourceInputStream
import androidx.media3.datasource.DataSpec
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.withContext

@UnstableApi
class FeedPrefetchController(
    private val playbackDataSourceFactory: DataSource.Factory,
    dispatcher: CoroutineDispatcher
) {

    private val scope = CoroutineScope(
        SupervisorJob() + dispatcher.limitedParallelism(1)
    )

    private sealed interface Msg {
        data class Prefetch(val uri: Uri) : Msg
        data class CancelAll(val reply: CompletableDeferred<Unit>) : Msg
    }

    @ObsoleteCoroutinesApi
    private val actor = scope.actor<Msg>(capacity = 32) {
        for (msg in channel) {
            when (msg) {
                is Msg.Prefetch -> prefetchInternal(msg.uri)
                is Msg.CancelAll -> {
                    coroutineContext.cancelChildren()
                    msg.reply.complete(Unit)
                }
            }
        }
    }

    @ObsoleteCoroutinesApi
    fun prefetch(uri: Uri) {
        actor.trySend(Msg.Prefetch(uri))
    }

    @ObsoleteCoroutinesApi
    suspend fun cancelAll() {
        val deferred = CompletableDeferred<Unit>()
        actor.send(Msg.CancelAll(deferred))
        deferred.await()
    }

    private suspend fun prefetchInternal(
        uri: Uri,
        maxBytes: Long = 3 * 1024 * 1024
    ) {
        withContext(Dispatchers.IO) {
            val dataSpec = DataSpec(uri)
            val dataSource = playbackDataSourceFactory.createDataSource()

            DataSourceInputStream(dataSource, dataSpec).use { input ->
                val buffer = ByteArray(32 * 1024)
                var total = 0L
                while (total < maxBytes) {
                    val read = input.read(buffer)
                    if (read == -1) break
                    total += read
                }
            }
        }
    }
}