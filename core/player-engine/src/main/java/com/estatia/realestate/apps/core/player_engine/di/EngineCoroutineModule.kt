package com.estatia.realestate.apps.core.player_engine.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class EngineScope

@Module
@InstallIn(SingletonComponent::class)
object EngineCoroutineModule {

    @Provides
    @Singleton
    @EngineScope
    fun provideEngineScope(): CoroutineScope {
        return CoroutineScope(
            SupervisorJob() + Dispatchers.Main.immediate
        )
    }
}
