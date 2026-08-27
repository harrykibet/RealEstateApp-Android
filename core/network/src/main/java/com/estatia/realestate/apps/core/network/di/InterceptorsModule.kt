package com.estatia.realestate.apps.core.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import dagger.multibindings.ElementsIntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InterceptorsModule {

    @Provides
    @Singleton
    @ElementsIntoSet
    @NetworkInterceptors
    fun provideDefaultInterceptors(): Set<Interceptor> = emptySet()
}
