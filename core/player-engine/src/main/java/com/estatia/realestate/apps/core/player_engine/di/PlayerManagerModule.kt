package com.estatia.realestate.apps.core.player_engine.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.upstream.BandwidthMeter
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter
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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayerManagerModule {

    @Provides
    @Singleton
    fun providesPlayerDispatcher(): CoroutineDispatcher {
        return Executors.newSingleThreadExecutor { r ->
            Thread(r, "PlayerManagerThread")
        }.asCoroutineDispatcher()
    }

    @Provides
    @Singleton
    fun provideEngineScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideBandwidthMeter(context: Context): BandwidthMeter {
        return DefaultBandwidthMeter.Builder(context).build()
    }
}
