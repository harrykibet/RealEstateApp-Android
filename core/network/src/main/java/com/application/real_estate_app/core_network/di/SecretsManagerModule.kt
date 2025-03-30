package com.application.real_estate_app.core_network.di

import com.application.real_estate_app.core_network.interfaces.IGoogleCloudSecretsManager
import com.application.real_estate_app.core_network.sources.GoogleCloudSecretsManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SecretsManagerModule{
    @Binds
    @Singleton
    abstract fun bindGoogleSecretsManager(googleSecretsManager: GoogleCloudSecretsManager): IGoogleCloudSecretsManager
}