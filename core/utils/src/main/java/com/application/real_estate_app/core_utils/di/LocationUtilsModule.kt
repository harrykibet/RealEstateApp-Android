package com.application.real_estate_app.core_utils.di

import com.application.real_estate_app.core_interface.ILocationUtils
import com.application.real_estate_app.core_utils.system.LocationUtils
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationUtilsModule {
    @Binds
    @Singleton
    abstract fun bindLocationUtils(locationUtils: LocationUtils) : ILocationUtils
}