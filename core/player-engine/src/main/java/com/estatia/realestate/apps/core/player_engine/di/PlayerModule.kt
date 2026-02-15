package com.estatia.realestate.apps.core.player_engine.di

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.upstream.BandwidthMeter
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter
import com.estatia.realestate.apps.core.domain.interfaces.IPlayer
import com.estatia.realestate.apps.core.player_engine.core.ABRStrategy
import com.estatia.realestate.apps.core.player_engine.core.PlayerManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@UnstableApi
@InstallIn(SingletonComponent::class)
abstract class PlayerModule {
    @Provides
    @Singleton
    fun provideBandwidthMeter(context: Context): BandwidthMeter {
        return DefaultBandwidthMeter.Builder(context).build()
    }

    @Provides
    @Singleton
    fun provideExoPlayer(
        context: Context,
        bandwidthMeter: BandwidthMeter,
        abrStrategy: ABRStrategy
    ): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setTrackSelector(DefaultTrackSelector(context, abrStrategy))
            .setBandwidthMeter(bandwidthMeter)
            .build()
    }

    @Binds
    @Singleton
    abstract fun bindExoplayer(exoplayer: PlayerManager): IPlayer
}
