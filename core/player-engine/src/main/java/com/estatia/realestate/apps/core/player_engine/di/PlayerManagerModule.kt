package com.estatia.realestate.apps.core.player_engine.di

import android.content.Context
import android.net.ConnectivityManager
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.upstream.BandwidthMeter
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter
import com.estatia.realestate.apps.core.common.interfaces.IBatteryManager
import com.estatia.realestate.apps.core.network.interfaces.INetworkStateProvider
import com.estatia.realestate.apps.core.player_engine.configuration.IPlaybackConfigurationProvider
import com.estatia.realestate.apps.core.player_engine.configuration.IPlayerConfigurationFactory
import com.estatia.realestate.apps.core.player_engine.configuration.PlaybackConfigurationProvider
import com.estatia.realestate.apps.core.player_engine.configuration.PlayerConfigurationFactory
import com.estatia.realestate.apps.core.player_engine.core.IPlayerManager
import com.estatia.realestate.apps.core.player_engine.core.PlayerManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import javax.inject.Qualifier
import javax.inject.Singleton
import com.estatia.realestate.apps.core.player_engine.streaming.*
import com.estatia.realestate.apps.core.player_engine.utils.AdaptivePlayerPoolSizingPolicy
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.player_engine.utils.IPlayerPoolSizingPolicy

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class StreamingDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IODispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PlayerDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EngineScope

@UnstableApi
@Module
@InstallIn(SingletonComponent::class)
abstract class PlayerManagerModule {

    // -------------------------------------------------------
    // @Binds — must be abstract, stays in the abstract class
    // -------------------------------------------------------

    @Binds
    @Singleton
    internal abstract fun bindPlayerManager(playerManager: PlayerManager): IPlayerManager

    @Binds
    @Singleton
    abstract fun bindPlayerPoolSizingPolicy(
        playerPoolSizingPolicy: AdaptivePlayerPoolSizingPolicy
    ): IPlayerPoolSizingPolicy

    @Binds
    @Singleton
    internal abstract fun bindStreamingPipeline(
        streamingPipeline: StreamingPipeline
    ): IStreamingPipeline

    @Binds
    @Singleton
    internal abstract fun bindCdnPolicy(cdnPolicy: CdnPolicy): ICdnPolicy

    @Binds
    @Singleton
    internal abstract fun bindPlayerConfigurationFactory(
        playerConfigurationFactory: PlayerConfigurationFactory
    ): IPlayerConfigurationFactory

    @Binds
    @Singleton
    internal abstract fun bindPlaybackConfigurationProvider(
        playbackConfigurationProvider: PlaybackConfigurationProvider
    ): IPlaybackConfigurationProvider

    @Binds
    @Singleton
    internal abstract fun bindCacheKeyFactory(
        cacheKeyFactory: DefaultCacheKeyFactory
    ): ICacheKeyFactory

    // -------------------------------------------------------
    // @Provides — must be static, goes in companion object
    // -------------------------------------------------------

    companion object {

        @Provides
        @Singleton
        @PlayerDispatcher
        fun providesPlayerDispatcher(): CoroutineDispatcher = Dispatchers.Main.immediate

        @Provides
        @Singleton
        @IODispatcher
        fun provideIODispatcher(): CoroutineDispatcher =
            Executors.newFixedThreadPool(2).asCoroutineDispatcher()

        @Provides
        @Singleton
        @StreamingDispatcher
        fun provideStreamingDispatcher(): CoroutineDispatcher =
            Executors.newSingleThreadExecutor { r ->
                Thread(r, "StreamingThread")
            }.asCoroutineDispatcher()

        @Provides
        @Singleton
        @EngineScope
        fun provideEngineScope(
            @IODispatcher dispatcher: CoroutineDispatcher
        ): CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)

        @Provides
        @Singleton
        fun provideBandwidthMeter(@ApplicationContext context: Context): BandwidthMeter =
            DefaultBandwidthMeter.Builder(context).build()

        @Provides
        @Singleton
        fun provideRandom(): kotlin.random.Random = kotlin.random.Random.Default

        @Provides
        @Singleton
        fun provideEnvironmentCoordinator(
            networkStateProvider: INetworkStateProvider,
            batteryManager: IBatteryManager,
            bandwidthMeter: BandwidthMeter,
            connectivityManager: ConnectivityManager
        ): EnvironmentCoordinator {
            return EnvironmentCoordinator(
                networkStateProvider = networkStateProvider,
                batteryManager = batteryManager,
                bandwidthMeter = bandwidthMeter,
                connectivityManager = connectivityManager
            )
        }
    }
}
