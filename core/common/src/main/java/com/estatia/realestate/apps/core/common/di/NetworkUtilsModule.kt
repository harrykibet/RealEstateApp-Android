package com.estatia.realestate.apps.core.common.di

import com.estatia.realestate.apps.core.common.system.NetworkUtils
import com.estatia.realestate.apps.core.common.interfaces.INetworkUtils
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