package com.application.real_estate_app.security.di

import com.google.cloud.kms.v1.KeyManagementServiceClient
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteDataSourceModule {
    @Provides
    @Singleton
    fun provideSecretManagerClient(): SecretManagerServiceClient {
        return SecretManagerServiceClient.create()
    }

    @Provides
    @Singleton
    fun provideKmsClient(): KeyManagementServiceClient {
        return KeyManagementServiceClient.create()
    }
}
