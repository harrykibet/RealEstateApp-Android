package com.application.real_estate_app.core_network.di

import com.application.real_estate_app.core_network.interfaces.IGoogleCloudKmsManager
import com.application.real_estate_app.core_network.sources.GoogleCloudKmsManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class KmsManagerModule{
    @Binds
    @Singleton
    abstract fun bindGoogleKmsManager(googleKmsManager: GoogleCloudKmsManager): IGoogleCloudKmsManager
}