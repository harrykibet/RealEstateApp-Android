package com.estatia.realestate.apps.core.testing_network.di

import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.network.interfaces.IExceptionMapper
import com.estatia.realestate.apps.core.network.interfaces.IRetryPolicy
import com.estatia.realestate.apps.core.testing_network.chaos.ChaosNetworkClient
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkChaosController
import com.estatia.realestate.apps.core.testing.chaos.concurrency.ConcurrencyChaosController
import com.estatia.realestate.apps.core.testing.chaos.lifecycle.LifecycleChaosController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing adversarial network infrastructure in integration tests.
 */
@Module
@InstallIn(SingletonComponent::class)
object TestNetworkModule {

    @Provides
    @Singleton
    fun provideChaosNetworkClient(
        networkChaos: NetworkChaosController,
        concurrencyChaos: ConcurrencyChaosController,
        lifecycleChaos: LifecycleChaosController,
        exceptionMapper: IExceptionMapper,
        retryPolicy: IRetryPolicy
    ): INetworkClient = ChaosNetworkClient(networkChaos, concurrencyChaos, lifecycleChaos, exceptionMapper, retryPolicy)

    @Provides
    @Singleton
    fun provideNetworkChaosController(): NetworkChaosController = NetworkChaosController()

    @Provides
    @Singleton
    fun provideConcurrencyChaosController(): ConcurrencyChaosController = ConcurrencyChaosController()

    @Provides
    @Singleton
    fun provideLifecycleChaosController(): LifecycleChaosController = LifecycleChaosController()
}
