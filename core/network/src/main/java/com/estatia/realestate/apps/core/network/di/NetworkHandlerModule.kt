package com.estatia.realestate.apps.core.network.di

import com.estatia.realestate.apps.core.network.NetworkHandler
import com.estatia.realestate.apps.core.network.interfaces.INetworkHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkHandlerModule {

    @Binds
    @Singleton
    abstract fun bindNetworkHandler(networkHandler: NetworkHandler): INetworkHandler
}
