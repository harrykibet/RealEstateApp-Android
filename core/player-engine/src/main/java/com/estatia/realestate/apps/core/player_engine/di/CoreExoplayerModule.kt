package com.estatia.realestate.apps.core.player_engine.di

import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.domain.interfaces.IExoplayer
import com.estatia.realestate.apps.core.player_engine.core.ExoPlayerInstanceManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@UnstableApi
@InstallIn(SingletonComponent::class)
abstract class CoreExoplayerModule {
    @Binds
    @Singleton
    abstract fun bindExoplayer(exoplayer: ExoPlayerInstanceManager): IExoplayer
}
