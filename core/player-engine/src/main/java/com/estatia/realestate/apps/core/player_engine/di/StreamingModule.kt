package com.estatia.realestate.apps.core.player_engine.di

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import com.estatia.realestate.apps.core.player_engine.streaming.CdnFailoverDataSourceFactory
import com.estatia.realestate.apps.core.player_engine.streaming.CdnSelector
import com.estatia.realestate.apps.core.player_engine.streaming.CdnHealthMonitor
import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.player_engine.streaming.DefaultLatencyMeasurer
import com.estatia.realestate.apps.core.player_engine.streaming.ILatencyMeasurer
import com.estatia.realestate.apps.core.player_engine.streaming.ChaosDataSourceFactory
import com.estatia.realestate.apps.core.player_engine.streaming.ICacheSizingPolicy
import com.estatia.realestate.apps.core.player_engine.streaming.AdaptiveCacheSizingPolicy
import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import com.estatia.realestate.apps.core.domain.interfaces.IConfigProvider
import com.estatia.realestate.apps.core.network.di.PlaybackClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OfflineCache

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PlaybackCache

@Module
@InstallIn(SingletonComponent::class)
@UnstableApi
object StreamingModule {

    @Provides
    @Singleton
    fun provideLatencyMeasurer(impl: DefaultLatencyMeasurer): ILatencyMeasurer = impl

    @Provides
    @Singleton
    fun provideDatabaseProvider(
        @ApplicationContext context: Context
    ): StandaloneDatabaseProvider = StandaloneDatabaseProvider(context)

    @Provides
    @Singleton
    fun provideUpstreamFactory(
        @ApplicationContext context: Context,
        @PlaybackClient okHttpClient: OkHttpClient,
        deviceUtils: IDeviceUtils,
        configProvider: IConfigProvider,
        cdnSelector: CdnSelector,
        healthMonitor: CdnHealthMonitor,
        logger: ILogger
    ): DataSource.Factory {
        val caps = mutableListOf<String>()
        if (deviceUtils.supportsAV1()) caps.add("av1")
        if (deviceUtils.supportsHEVC()) caps.add("hevc")
        if (deviceUtils.supports10BitHdr()) caps.add("hdr10")
        if (context.packageManager.hasSystemFeature("android.hardware.dolbyvision")) caps.add("dolbyvision")
        
        val baseFactory = OkHttpDataSource.Factory(okHttpClient)
            .setUserAgent(Util.getUserAgent(context, "Estatia"))
            .setDefaultRequestProperties(mapOf(
                "Accept" to "*/*",
                "X-Estatia-Capabilities" to caps.joinToString(",")
            ))

        // 🏎️ High-Integrity Failover: Wrap the network factory in a failover decorator
        // that handles segment-level CDN switching.
        val failoverFactory = CdnFailoverDataSourceFactory(baseFactory, cdnSelector, healthMonitor, logger)

        // 🏎️ Chaos Injection: Wrap the factory in a Chaos decorator in debug builds
        // to enable real-world failure simulation.
        return if (com.estatia.realestate.apps.core.player_engine.BuildConfig.DEBUG) {
            ChaosDataSourceFactory(failoverFactory, configProvider)
        } else {
            failoverFactory
        }
    }

    @Provides
    @Singleton
    fun provideCacheSizingPolicy(impl: AdaptiveCacheSizingPolicy): ICacheSizingPolicy = impl

    @Provides
    @Singleton
    @PlaybackCache
    fun providePlaybackCache(
        @ApplicationContext context: Context,
        databaseProvider: StandaloneDatabaseProvider,
        cacheSizingPolicy: ICacheSizingPolicy
    ): SimpleCache =
        SimpleCache(
            File(context.cacheDir, "playback_cache"),
            LeastRecentlyUsedCacheEvictor(cacheSizingPolicy.calculateCacheSizeBytes()),
            databaseProvider
        )

    @Provides
    @Singleton
    @OfflineCache
    fun provideOfflineCache(
        @ApplicationContext context: Context,
        databaseProvider: StandaloneDatabaseProvider
    ): SimpleCache =
        SimpleCache(
            File(context.filesDir, "offline_cache"),
            NoOpCacheEvictor(),
            databaseProvider
        )

    // StreamingModule.kt — add a qualified caching factory alongside the existing plain one
    @Provides
    @Singleton
    @PlaybackCache
    fun providePlaybackCacheDataSourceFactory(
        @PlaybackCache playbackCache: SimpleCache,
        upstreamFactory: DataSource.Factory
    ): DataSource.Factory =
        CacheDataSource.Factory()
            .setCache(playbackCache)
            .setUpstreamDataSourceFactory(upstreamFactory)

    @Provides
    @Singleton
    fun provideDownloadExecutor(): ExecutorService =
        Executors.newFixedThreadPool(2)

    @Provides
    @Singleton
    fun provideDownloadManager(
        @ApplicationContext context: Context,
        databaseProvider: StandaloneDatabaseProvider,
        @OfflineCache offlineCache: SimpleCache,
        upstreamFactory: DataSource.Factory,
        executor: ExecutorService
    ): DownloadManager =
        DownloadManager(
            context,
            databaseProvider,
            offlineCache,
            upstreamFactory,
            executor
        )

    @Provides
    @Singleton
    fun provideMediaSourceFactory(
        @PlaybackCache playbackCache: SimpleCache,
        @OfflineCache offlineCache: SimpleCache,
        upstreamFactory: DataSource.Factory
    ): MediaSource.Factory {

        val layeredFactory =
            CacheDataSource.Factory()
                .setCache(playbackCache)
                .setUpstreamDataSourceFactory(
                    CacheDataSource.Factory()
                        .setCache(offlineCache)
                        .setUpstreamDataSourceFactory(upstreamFactory)
                )

        return DefaultMediaSourceFactory(layeredFactory)
    }
}
