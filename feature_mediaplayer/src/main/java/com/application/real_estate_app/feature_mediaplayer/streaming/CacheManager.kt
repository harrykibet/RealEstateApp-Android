package com.application.real_estate_app.feature_mediaplayer.streaming

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.*
import androidx.media3.datasource.cache.*
import androidx.media3.database.StandaloneDatabaseProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(markerClass = [UnstableApi::class])
@Singleton
class CacheManager @Inject constructor(
    private val context: Context
) {
    private val cacheDir: File = File(context.cacheDir, "media_cache")
    private val databaseProvider = StandaloneDatabaseProvider(context)

    private val cache: SimpleCache by lazy {
        SimpleCache(
            cacheDir,
            LeastRecentlyUsedCacheEvictor(MAX_CACHE_SIZE),
            databaseProvider
        ).also {
            if (!cacheDir.exists()) cacheDir.mkdirs()
        }
    }

    private val upstreamFactory: DataSource.Factory = DefaultHttpDataSource.Factory()

    private val cacheDataSourceFactory: DataSource.Factory by lazy {
        CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(upstreamFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    fun createCacheDataSourceFactory(): DataSource.Factory = cacheDataSourceFactory

    fun isCached(url: String): Boolean {
        return cache.isCached(url, 0, C.LENGTH_UNSET.toLong())
    }

    fun removeFromCache(url: String) {
        cache.removeResource(url)
    }

    fun clearEntireCache() {
        cache.release()
        SimpleCache.delete(cacheDir, databaseProvider)
    }

    fun autoManageCache() {
        val usedSpace = cache.cacheSpace
        if (usedSpace > MAX_CACHE_SIZE * 0.9) {
            cache.keys.forEach { key ->
                val lruEntries = cache.getCachedSpans(key)
                    .sortedBy { it.position }
                    .take(5)

                lruEntries.forEach { cache.removeResource(it.key) }
            }
        }
    }

    fun getCacheStatus(url: String): CacheStatus {
        val totalBytes = cache.getCachedLength(url, 0, C.LENGTH_UNSET.toLong())
        val cachedBytes = cache.getCachedBytes(url, 0, totalBytes)

        return when {
            cachedBytes <= 0 -> CacheStatus.NOT_CACHED
            totalBytes in 1..cachedBytes -> CacheStatus.FULLY_CACHED
            else -> CacheStatus.PARTIALLY_CACHED(totalBytes, cachedBytes)
        }
    }

    fun prefetch(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dataSpec = DataSpec.Builder().setUri(url).build()
                val dataSource = cacheDataSourceFactory.createDataSource()
                DataSourceInputStream(dataSource, dataSpec).use { inputStream ->
                    val buffer = ByteArray(8192)
                    while (inputStream.read(buffer) != -1) { /* Read to cache */ }
                }
            } catch (e: Exception) {
                e.printStackTrace() // Handle error properly in production
            }
        }
    }

    sealed class CacheStatus {
        object NOT_CACHED : CacheStatus()
        object FULLY_CACHED : CacheStatus()
        data class PARTIALLY_CACHED(val totalBytes: Long, val cachedBytes: Long) : CacheStatus()
    }

    companion object {
        private const val MAX_CACHE_SIZE = 512 * 1024 * 1024L // 512 MB
    }
}
