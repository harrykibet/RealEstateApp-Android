package com.estatia.realestate.apps.core.common.di

import com.estatia.realestate.apps.core.common.system.Dispatcher
import com.estatia.realestate.apps.core.common.system.EstatiaDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope

/**
 * Module providing high-level application [CoroutineScope]s.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Manage the lifecycle of long-running asynchronous tasks.
 * - Resilience: Uses [SupervisorJob] to prevent failure propagation across sibling jobs.
 * - Concurrency: Bound to the global application lifecycle via [Singleton].
 */
@Module
@InstallIn(SingletonComponent::class)
internal object CoroutineScopesModule {
    @Provides
    @Singleton
    @ApplicationScope
    fun providesCoroutineScope(
        @Dispatcher(EstatiaDispatchers.Default) dispatcher: CoroutineDispatcher,
    ): CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)
}
