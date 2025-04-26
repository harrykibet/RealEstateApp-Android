package com.application.real_estate_app.core.di

import com.application.real_estate_app.core.domain.interfaces.IDeviceUtils
import com.application.real_estate_app.core.utils.system.DeviceUtils
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DeviceUtilsModule {
    @Binds
    @Singleton
    abstract fun bindDeviceUtils(deviceUtils: DeviceUtils) : IDeviceUtils
}