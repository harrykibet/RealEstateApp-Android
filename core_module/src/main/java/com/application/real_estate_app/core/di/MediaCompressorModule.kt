package com.application.real_estate_app.core.di

import com.application.real_estate_app.core.domain.interfaces.IMediaCompressor
import com.application.real_estate_app.core.utils.media.MediaCompressor
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