package com.estatia.realestate.apps.core.player_engine.di

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.upstream.BandwidthMeter
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter
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

    @Binds
    @Singleton
    abstract fun bindExoplayer(exoplayer: PlayerManager): IPlayer
}
