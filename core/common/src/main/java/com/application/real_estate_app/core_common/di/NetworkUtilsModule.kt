package com.application.real_estate_app.core_common.di

import com.application.real_estate_app.core_common.system.NetworkUtils
import com.application.real_estate_app.core_common.interfaces.INetworkUtils
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