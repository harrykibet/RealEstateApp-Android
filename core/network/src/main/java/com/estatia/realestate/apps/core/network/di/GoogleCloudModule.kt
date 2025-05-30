package com.estatia.realestate.apps.core.network.di

import com.google.cloud.kms.v1.KeyManagementServiceClient
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GoogleCloudModule {
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
