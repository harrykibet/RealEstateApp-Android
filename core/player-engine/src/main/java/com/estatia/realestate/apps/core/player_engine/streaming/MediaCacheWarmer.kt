package com.estatia.realestate.apps.core.player_engine.streaming

import android.net.Uri
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSourceInputStream
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.HttpDataSource
import com.estatia.realestate.apps.core.common.events.EventTypes
import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.domain.interfaces.IAnalyticsTracker
import com.estatia.realestate.apps.core.player_engine.di.EngineScope
import com.estatia.realestate.apps.core.player_engine.di.IODispatcher
import com.estatia.realestate.apps.core.player_engine.di.PlaybackCache
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class MediaCacheWarmer @Inject constructor(
    @param:PlaybackCache private val playbackDataSourceFactory: DataSource.Factory,
    private val environmentCoordinator: EnvironmentCoordinator,
    private val cdnHealthMonitor: CdnHealthMonitor,
    private val cacheKeyFactory: ICacheKeyFactory,
    private val deviceUtils: IDeviceUtils,
    private val analyticsTracker: IAnalyticsTracker,
    @param:EngineScope private val scope: CoroutineScope,
    @param:IODispatcher private val ioDispatcher: CoroutineDispatcher,
    private val logger: ILogger
) : AutoCloseable {

    private val requests = MutableSharedFlow<WarmRequest>(
        extraBufferCapacity = 16,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val environment: StateFlow<EnvironmentState>
        get() = environmentCoordinator.environment

    private var visibleJob: Job? = null
    private var nextJob: Job? = null
    private var previousJob: Job? = null
    private var lowJob: Job? = null
    
    private var buffering = false

    init {
        scope.launch {
            requests.collect { request ->
                when (request.priority) {
                    WarmPriority.VISIBLE -> handleVisible(request)
                    WarmPriority.NEXT -> handleNext(request)
                    WarmPriority.PREVIOUS -> handlePrevious(request)
                    WarmPriority.LOW -> handleLow(request)
                }
            }
        }
    }

    fun prefetch(mediaId: String, uri: Uri, priority: WarmPriority, qualityHint: String? = null) {
        requests.tryEmit(WarmRequest(uri, priority, mediaId, qualityHint))
    }

    fun onBufferingStarted() {
        buffering = true
        nextJob?.cancel()
        nextJob = null
        lowJob?.cancel()
        lowJob = null
    }

    fun onBufferingEnded() {
        buffering = false
    }

    private fun handleVisible(request: WarmRequest) {
        visibleJob?.cancel()

        visibleJob = scope.launch {
            prefetchInternal(
                mediaId = request.mediaId,
                uri = request.uri,
                qualityHint = request.qualityHint,
                maxBytes = adaptivePrefetchSize(WarmPriority.VISIBLE)
            )
        }
    }

    private fun handleNext(request: WarmRequest) {
        if (buffering) return

        nextJob?.cancel()

        nextJob = scope.launch {
            prefetchInternal(
                mediaId = request.mediaId,
                uri = request.uri,
                qualityHint = request.qualityHint,
                maxBytes = adaptivePrefetchSize(WarmPriority.NEXT)
            )
        }
    }

    private fun handlePrevious(request: WarmRequest) {
        if (buffering) return

        previousJob?.cancel()

        previousJob = scope.launch {
            prefetchInternal(
                mediaId = request.mediaId,
                uri = request.uri,
                qualityHint = request.qualityHint,
                maxBytes = adaptivePrefetchSize(WarmPriority.PREVIOUS)
            )
        }
    }

    private fun handleLow(request: WarmRequest) {
        if (buffering) return

        lowJob?.cancel()

        lowJob = scope.launch {
            prefetchInternal(
                mediaId = request.mediaId,
                uri = request.uri,
                qualityHint = request.qualityHint,
                maxBytes = adaptivePrefetchSize(WarmPriority.LOW)
            )
        }
    }

    /**
     * Core policy engine: derives cache size from environment snapshot and priority.
     */
    private fun adaptivePrefetchSize(priority: WarmPriority): Long {
        val env = environment.value

        val throttled = env.shouldThrottlePerformance
        val metered = env.isMetered
        val throughput = env.estimatedThroughputBps

        val baseSize = when {
            throttled -> 1L * 1024 * 1024
            metered -> 2L * 1024 * 1024
            throughput > 10_000_000L -> 5L * 1024 * 1024
            else -> 3L * 1024 * 1024
        }

        return when (priority) {
            WarmPriority.VISIBLE -> baseSize
            WarmPriority.NEXT -> (baseSize * 0.7).toLong()
            WarmPriority.PREVIOUS -> (baseSize * 0.4).toLong()
            WarmPriority.LOW -> (baseSize * 0.2).toLong()
        }
    }

    /**
     * Safe bounded prefetch using DataSource streaming.
     * Fully cancellable and IO-isolated.
     */
    private suspend fun prefetchInternal(
        mediaId: String,
        uri: Uri,
        qualityHint: String?,
        maxBytes: Long
    ) = withContext(ioDispatcher) {

        try {
            val stableKey = cacheKeyFactory.resolveStableKey(uri, mediaId, qualityHint)

            val dataSpec = DataSpec.Builder()
                .setUri(uri)
                .setKey(stableKey)
                .setLength(maxBytes)
                .setFlags(DataSpec.FLAG_ALLOW_CACHE_FRAGMENTATION)
                .build()

            val dataSource = playbackDataSourceFactory.createDataSource()

            DataSourceInputStream(dataSource, dataSpec).use { input ->

                val buffer = ByteArray(32 * 1024)
                var total = 0L

                while (
                    total < maxBytes &&
                    currentCoroutineContext().isActive
                ) {
                    val read = input.read(buffer)
                    if (read == -1) break
                    total += read
                }
            }
        } catch (e: Exception) {
            val message = when (e) {
                is HttpDataSource.InvalidResponseCodeException -> {
                    // Feed failure back to CDN health monitor
                    val baseUrl = "${uri.scheme}://${uri.authority}"
                    cdnHealthMonitor.reportExternalFailure(baseUrl)
                    "Failed to prefetch uri: $uri. Max bytes: $maxBytes. Response code: ${e.responseCode}. Data: ${e.dataSpec}"
                }
                is java.io.IOException -> {
                    // Check for disk full condition
                    val isDiskFull = e.message?.contains("ENOSPC", ignoreCase = true) == true ||
                            e.message?.contains("No space left on device", ignoreCase = true) == true ||
                            deviceUtils.getAvailableStorageMB() < 50 // Critically low

                    if (isDiskFull) {
                        analyticsTracker.logEvent(
                            message = "MediaCacheWarmer",
                            eventType = EventTypes.EVENT_DISK_FULL,
                            customMetadata = mapOf("uri" to uri.toString())
                        )
                        logger.w("MediaCacheWarmer", "Prefetch failed: Disk is full. uri: $uri")
                        "Failed to prefetch uri: $uri. Max bytes: $maxBytes (Disk Full)"
                    } else {
                        // Feed network/timeout failure back to CDN health monitor
                        val baseUrl = "${uri.scheme}://${uri.authority}"
                        cdnHealthMonitor.reportExternalFailure(baseUrl)
                        "Failed to prefetch uri: $uri. Max bytes: $maxBytes (IO Error)"
                    }
                }
                else -> {
                    "Failed to prefetch uri: $uri. Max bytes: $maxBytes"
                }
            }
            logger.e("MediaCacheWarmer", message, e)
        }
    }

    suspend fun cancelAllJobs() {
        visibleJob?.cancelAndJoin()
        nextJob?.cancelAndJoin()
        previousJob?.cancelAndJoin()
        lowJob?.cancelAndJoin()
        
        visibleJob = null
        nextJob = null
        previousJob = null
        lowJob = null
    }

    override fun close() {
        visibleJob?.cancel()
        nextJob?.cancel()
        previousJob?.cancel()
        lowJob?.cancel()
    }
}
