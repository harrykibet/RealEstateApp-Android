package com.estatia.realestate.apps.core.common.di

import com.estatia.realestate.apps.core.common.interfaces.ILocationUtils
import com.estatia.realestate.apps.core.common.system.LocationUtils
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
