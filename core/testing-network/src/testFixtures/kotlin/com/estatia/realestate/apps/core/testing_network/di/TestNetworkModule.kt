package com.estatia.realestate.apps.core.testing_network.di

import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.network.interfaces.IExceptionMapper
import com.estatia.realestate.apps.core.network.interfaces.IRetryPolicy
import com.estatia.realestate.apps.core.testing_network.chaos.ChaosNetworkClient
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkChaosController
import com.estatia.realestate.apps.core.testing.chaos.concurrency.ConcurrencyChaosController
import com.estatia.realestate.apps.core.testing.chaos.lifecycle.LifecycleChaosController
import com.estatia.realestate.apps.core.network.interfaces.IAuthRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.IPropertyRemoteDatasource
import com.estatia.realestate.apps.core.network.interfaces.ISearchRemoteDataSource
import com.estatia.realestate.apps.core.testing_network.chaos.EstatiaTestScenario
import com.estatia.realestate.apps.core.testing_network.fake.source.FakeAuthRemoteDataSource
import com.estatia.realestate.apps.core.testing_network.fake.source.FakePropertyRemoteDataSource
import com.estatia.realestate.apps.core.testing_network.fake.source.FakeSearchRemoteDataSource
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
    fun provideEstatiaTestScenario(
        networkChaos: NetworkChaosController,
        authFake: FakeAuthRemoteDataSource,
        propertyFake: FakePropertyRemoteDataSource,
        searchFake: FakeSearchRemoteDataSource
    ): EstatiaTestScenario = EstatiaTestScenario(networkChaos, authFake, propertyFake, searchFake)

    @Provides
    @Singleton
    fun provideAuthRemoteDataSource(
        fake: FakeAuthRemoteDataSource
    ): IAuthRemoteDataSource = fake

    @Provides
    @Singleton
    fun providePropertyRemoteDataSource(
        fake: FakePropertyRemoteDataSource
    ): IPropertyRemoteDatasource = fake

    @Provides
    @Singleton
    fun provideSearchRemoteDataSource(
        fake: FakeSearchRemoteDataSource
    ): ISearchRemoteDataSource = fake

    @Provides
    @Singleton
    fun provideFakeAuthRemoteDataSource(
        concurrencyChaos: ConcurrencyChaosController,
        lifecycleChaos: LifecycleChaosController
    ): FakeAuthRemoteDataSource = FakeAuthRemoteDataSource(concurrencyChaos, lifecycleChaos)

    @Provides
    @Singleton
    fun provideFakePropertyRemoteDataSource(
        concurrencyChaos: ConcurrencyChaosController,
        lifecycleChaos: LifecycleChaosController
    ): FakePropertyRemoteDataSource = FakePropertyRemoteDataSource(concurrencyChaos, lifecycleChaos)

    @Provides
    @Singleton
    fun provideFakeSearchRemoteDataSource(
        concurrencyChaos: ConcurrencyChaosController,
        lifecycleChaos: LifecycleChaosController
    ): FakeSearchRemoteDataSource = FakeSearchRemoteDataSource(concurrencyChaos, lifecycleChaos)

    @Provides
    @Singleton
    fun provideLifecycleChaosController(): LifecycleChaosController = LifecycleChaosController()
}
