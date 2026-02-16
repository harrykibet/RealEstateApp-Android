package com.estatia.realestate.apps.core.player_engine.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayerDispatcherModule {

    @Provides
    @Singleton
    fun providesPlayerDispatcher(): CoroutineDispatcher {
        return Executors.newSingleThreadExecutor { r ->
            Thread(r, "PlayerManagerThread").apply { isDaemon = true }
        }.asCoroutineDispatcher()
    }
}
