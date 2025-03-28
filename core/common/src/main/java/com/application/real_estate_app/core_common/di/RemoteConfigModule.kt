package com.application.real_estate_app.core_common.di

import com.application.real_estate_app.core_common.interfaces.IRemoteConfigManager
import com.application.real_estate_app.core_common.firebase.RemoteConfigManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteConfigModule {
    @Binds
    @Singleton
    abstract fun bindRemoteConfigManager(remoteConfigManager: RemoteConfigManager): IRemoteConfigManager
}
