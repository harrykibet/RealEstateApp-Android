package com.application.real_estate_app.core.di

import com.application.real_estate_app.core.domain.interfaces.INetworkUtils
import com.application.real_estate_app.core.utils.network.NetworkUtils
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
    abstract fun bindNetworkUtils(networkUtils: NetworkUtils) : INetworkUtils
}