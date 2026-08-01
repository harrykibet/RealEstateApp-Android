package com.estatia.realestate.apps.core.common.di

import com.estatia.realestate.apps.core.common.interfaces.IMediaCompressor
import com.estatia.realestate.apps.core.common.media.MediaCompressor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MediaCompressorModule {
    @Binds
    @Singleton
    abstract fun bindMediaCompressor(mediaCompressor: MediaCompressor): IMediaCompressor
}
