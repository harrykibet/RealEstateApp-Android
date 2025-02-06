package com.application.real_estate_app.feature_mediaplayer.di

import androidx.media3.common.util.UnstableApi
import com.application.real_estate_app.core.domain.interfaces.IExoplayer
import com.application.real_estate_app.feature_mediaplayer.core.ExoPlayerInstanceManager
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
