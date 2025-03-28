package com.application.real_estate_app.core_network.di

import com.application.real_estate_app.core_network.NetworkHandler
import com.application.real_estate_app.core_network.interfaces.INetworkHandler
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
