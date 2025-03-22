package com.application.real_estate_app.security.di

import com.application.real_estate_app.security.data.sources.remote.GoogleCloudKmsManager
import com.application.real_estate_app.security.domain.interfaces.IGoogleCloudKmsManager
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