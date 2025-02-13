package com.application.real_estate_app.core.di

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
}
