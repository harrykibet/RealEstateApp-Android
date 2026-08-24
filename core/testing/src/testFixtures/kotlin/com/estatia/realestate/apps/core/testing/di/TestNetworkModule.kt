package com.estatia.realestate.apps.core.testing.di

import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.network.interfaces.IExceptionMapper
import com.estatia.realestate.apps.core.testing.chaos.network.ChaosNetworkClient
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkChaosController
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/**
 * Example Hilt module replacement for integration testing.
 * This is provided as a template and should be used in actual test modules.
 */
// @Module
// @TestInstallIn(
//     components = [SingletonComponent::class],
//     replaces = [ProdNetworkModule::class]
// )
object TestNetworkModuleTemplate {

    @Provides
    @Singleton
    fun provideChaosNetworkClient(
        chaosController: NetworkChaosController,
        exceptionMapper: IExceptionMapper
    ): INetworkClient = ChaosNetworkClient(chaosController, exceptionMapper)

    @Provides
    @Singleton
    fun provideNetworkChaosController(): NetworkChaosController = NetworkChaosController()
}
