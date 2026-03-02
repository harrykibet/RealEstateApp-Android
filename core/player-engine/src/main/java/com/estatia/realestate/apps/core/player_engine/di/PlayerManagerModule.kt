package com.estatia.realestate.apps.core.player_engine.di

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.upstream.BandwidthMeter
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter
import com.estatia.realestate.apps.core.player_engine.configuration.IPlaybackConfigurationProvider
import com.estatia.realestate.apps.core.player_engine.configuration.IPlayerConfigurationFactory
import com.estatia.realestate.apps.core.player_engine.configuration.PlaybackConfigurationProvider
import com.estatia.realestate.apps.core.player_engine.configuration.PlayerConfigurationFactory
import com.estatia.realestate.apps.core.player_engine.core.IPlayerManager
import com.estatia.realestate.apps.core.player_engine.core.PlayerManager
import com.estatia.realestate.apps.core.player_engine.streaming.CdnPolicy
import com.estatia.realestate.apps.core.player_engine.streaming.ICdnPolicy
import com.estatia.realestate.apps.core.player_engine.streaming.IStreamingPipeline
import com.estatia.realestate.apps.core.player_engine.streaming.StreamingPipeline
import com.estatia.realestate.apps.core.player_engine.utils.AdaptivePlayerPoolSizingPolicy
import com.estatia.realestate.apps.core.player_engine.utils.IPlayerPoolSizingPolicy
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class StreamingDispatcher

@UnstableApi
@Module
@InstallIn(SingletonComponent::class)
abstract class PlayerManagerModule {

    @Provides
    @Singleton
    fun providesPlayerDispatcher(): CoroutineDispatcher {
        return Executors.newSingleThreadExecutor { r ->
            Thread(r, "PlayerManagerThread")
        }.asCoroutineDispatcher()
    }

    @Provides
    @Singleton
    @StreamingDispatcher
    fun provideStreamingDispatcher(): CoroutineDispatcher =
        Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    @Provides
    @Singleton
    fun provideEngineScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Provides
    @Singleton
    fun provideBandwidthMeter(context: Context): BandwidthMeter {
        return DefaultBandwidthMeter.Builder(context).build()
    }

    @Binds
    @Singleton
    abstract fun bindPlayerManager(playerManager: PlayerManager): IPlayerManager

    @Binds
    @Singleton
    abstract fun bindPlayerPoolSizingPolicy(
        playerPoolSizingPolicy: AdaptivePlayerPoolSizingPolicy
    ): IPlayerPoolSizingPolicy

    @Binds
    @Singleton
    abstract fun bindStreamingPipeline(
        streamingPipeline: StreamingPipeline
    ): IStreamingPipeline

    @Binds
    @Singleton
    abstract fun bindCdnPolicy(
        cdnPolicy: CdnPolicy
    ) : ICdnPolicy

    @Binds
    @Singleton
    abstract fun bindPlayerConfigurationFactory(
        playerConfigurationFactory:
        PlayerConfigurationFactory
    ): IPlayerConfigurationFactory

    @Binds
    @Singleton
    abstract fun bindPlaybackConfigurationProvider(
        playbackConfigurationProvider:
        PlaybackConfigurationProvider
    ): IPlaybackConfigurationProvider
}
