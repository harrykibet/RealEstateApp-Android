package com.estatia.realestate.apps.core.player_engine.di

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
    fun provideDatabaseProvider(
        @ApplicationContext context: Context
    ): StandaloneDatabaseProvider = StandaloneDatabaseProvider(context)

    @Provides
    @Singleton
    fun provideUpstreamFactory(): DataSource.Factory =
        DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true)

    @Provides
    @Singleton
    @PlaybackCache
    fun providePlaybackCache(
        @ApplicationContext context: Context,
        databaseProvider: StandaloneDatabaseProvider
    ): SimpleCache =
        SimpleCache(
            File(context.cacheDir, "playback_cache"),
            LeastRecentlyUsedCacheEvictor(512L * 1024 * 1024),
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