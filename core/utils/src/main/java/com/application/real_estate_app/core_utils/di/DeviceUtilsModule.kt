package com.application.real_estate_app.core_utils.di

import com.application.real_estate_app.core_interface.IDeviceUtils
import com.application.real_estate_app.core_utils.system.DeviceUtils
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