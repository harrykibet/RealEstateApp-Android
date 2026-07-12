package com.estatia.realestate.apps.core.security.di

import com.estatia.realestate.apps.core.security.BuildConfigApiKeyProvider
import com.estatia.realestate.apps.core.security.interfaces.ApiKeyProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideApiKeyProvider(): ApiKeyProvider =
        BuildConfigApiKeyProvider()
}