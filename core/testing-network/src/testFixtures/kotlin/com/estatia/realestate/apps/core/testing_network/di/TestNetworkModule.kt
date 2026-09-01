package com.estatia.realestate.apps.core.testing_network.di

import com.estatia.realestate.apps.core.network.di.*
import com.estatia.realestate.apps.core.network.interfaces.*
import com.estatia.realestate.apps.core.testing_network.chaos.ChaosNetworkClient
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkChaosController
import com.estatia.realestate.apps.core.testing.chaos.concurrency.ConcurrencyChaosController
import com.estatia.realestate.apps.core.testing.chaos.lifecycle.LifecycleChaosController
import com.estatia.realestate.apps.core.testing.chaos.resources.ChaosResourceController
import com.estatia.realestate.apps.core.testing_network.chaos.EstatiaTestScenario
import com.estatia.realestate.apps.core.testing_network.chaos.interceptors.ChaosInterceptor
import com.estatia.realestate.apps.core.testing_network.fake.source.FakeAuthRemoteDataSource
import com.estatia.realestate.apps.core.testing_network.fake.source.FakePropertyRemoteDataSource
import com.estatia.realestate.apps.core.testing_network.fake.source.FakeSearchRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import javax.inject.Singleton

/**
 * Hilt module for providing adversarial network infrastructure in integration tests.
 * Complements production modules by providing a ChaosInterceptor and Fake sources.
 * 
 * ⚠️ INTEGRATION TEST HYGIENE:
 * Several components provided here (ChaosNetworkClient, Controllers) are @Singleton 
 * to ensure consistency across the application. However, they carry mutable chaos 
 * state (e.g., held requests). 
 * 
 * To prevent cross-test contamination, ALWAYS call [EstatiaTestScenario.reset()] 
 * in your test's @Before or @After block.
 */
@Module
@dagger.hilt.InstallIn(SingletonComponent::class)
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
    @IntoSet
    @NetworkInterceptors
    fun bindChaosInterceptor(
        interceptor: ChaosInterceptor
    ): Interceptor = interceptor

    @Provides
    @Singleton
    fun provideChaosInterceptor(
        networkChaos: NetworkChaosController
    ): ChaosInterceptor = ChaosInterceptor(networkChaos)

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
        networkClient: INetworkClient,
        resourceController: ChaosResourceController,
        authFake: FakeAuthRemoteDataSource,
        propertyFake: FakePropertyRemoteDataSource,
        searchFake: FakeSearchRemoteDataSource
    ): EstatiaTestScenario = EstatiaTestScenario(networkChaos, networkClient, resourceController, authFake, propertyFake, searchFake)

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
    fun provideChaosResourceController(): ChaosResourceController = ChaosResourceController()

    @Provides
    @Singleton
    fun provideLifecycleChaosController(
        resourceController: ChaosResourceController
    ): LifecycleChaosController = LifecycleChaosController(resourceController)
}
