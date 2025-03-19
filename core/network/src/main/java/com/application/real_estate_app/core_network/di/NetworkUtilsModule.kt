package com.application.real_estate_app.core_network.di

import com.application.real_estate_app.core_interface.INetworkUtils
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkUtilsModule {
    @Binds
    @Singleton
    abstract fun bindNetworkUtils(networkUtils: com.application.real_estate_app.core_network.NetworkUtils) : INetworkUtils
}