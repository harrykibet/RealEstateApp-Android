package com.estatia.realestate.apps.core.player_engine.streaming

import android.content.Context
import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.DataSourceInputStream
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import com.estatia.realestate.apps.core.common.logs.Logger
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class StreamingPipeline @Inject constructor(
    @ApplicationContext context: Context
) : IStreamingPipeline {

    // -----------------------------------------
    // Confinement Scope
    // -----------------------------------------

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO.limitedParallelism(1)
    )

    // -----------------------------------------
    // Cache Setup
    // -----------------------------------------

    private val cacheDir = File(context.cacheDir, CACHE_FOLDER)
    private val databaseProvider = StandaloneDatabaseProvider(context)

    private val simpleCache: SimpleCache by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        if (!cacheDir.exists()) cacheDir.mkdirs()

        SimpleCache(
            cacheDir,
            LeastRecentlyUsedCacheEvictor(MAX_CACHE_BYTES),
            databaseProvider
        )
    }

    private val upstreamFactory: DataSource.Factory =
        DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)

    private val cacheDataSourceFactory: DataSource.Factory by lazy {
        CacheDataSource.Factory()
            .setCache(simpleCache)
            .setUpstreamDataSourceFactory(upstreamFactory)
            .setFlags(0)
    }

    private val mediaSourceFactory: MediaSource.Factory by lazy {
        DefaultMediaSourceFactory(cacheDataSourceFactory)
    }

    // -----------------------------------------
    // Actor Messages
    // -----------------------------------------

    private sealed interface Msg {
        data class Prefetch(val uri: Uri) : Msg
        data class CacheSize(val reply: CompletableDeferred<Long>) : Msg
        data class IsCached(
            val key: String,
            val reply: CompletableDeferred<Boolean>
        ) : Msg
    }

    private val actor = scope.actor<Msg>(capacity = Channel.UNLIMITED) {
        for (msg in channel) {
            when (msg) {
                is Msg.Prefetch -> handlePrefetch(msg.uri)
                is Msg.CacheSize -> msg.reply.complete(simpleCache.cacheSpace)
                is Msg.IsCached ->
                    msg.reply.complete(
                        simpleCache.isCached(msg.key, 0, C.LENGTH_UNSET.toLong())
                    )
            }
        }
    }

    // -----------------------------------------
    // Public API
    // -----------------------------------------

    override fun mediaSourceFactory(): MediaSource.Factory = mediaSourceFactory

    override fun createMediaItem(
        uri: Uri,
        mediaType: MediaType
    ): MediaItem {

        val builder = MediaItem.Builder()
            .setUri(uri)

        if (mediaType == MediaType.LIVE) {
            val liveConfig = MediaItem.LiveConfiguration.Builder()
                .setTargetOffsetMs(1000L)
                .setMinPlaybackSpeed(0.97f)
                .setMaxPlaybackSpeed(1.03f)
                .build()

            builder.setLiveConfiguration(liveConfig)
        }

        return builder.build()
    }

    override fun prefetch(uri: Uri) {
        actor.trySend(Msg.Prefetch(uri))
    }

    override suspend fun cacheSizeBytes(): Long {
        val deferred = CompletableDeferred<Long>()
        actor.send(Msg.CacheSize(deferred))
        return deferred.await()
    }

    override suspend fun isFullyCached(cacheKey: String): Boolean {
        val deferred = CompletableDeferred<Boolean>()
        actor.send(Msg.IsCached(cacheKey, deferred))
        return deferred.await()
    }

    // -----------------------------------------
    // Internal
    // -----------------------------------------

    private suspend fun handlePrefetch(uri: Uri) {
        try {
            val dataSpec = DataSpec.Builder()
                .setUri(uri)
                .setFlags(DataSpec.FLAG_ALLOW_GZIP)
                .build()

            val dataSource = cacheDataSourceFactory.createDataSource()

            DataSourceInputStream(dataSource, dataSpec).use { input ->
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                while (input.read(buffer) != -1) {
                    // fully actor-confined
                }
            }
        } catch (_: Throwable) {
            // handle failure
        }
    }

    companion object {
        private const val CACHE_FOLDER = "media_cache"
        private const val MAX_CACHE_BYTES = 512L * 1024 * 1024
    }
}