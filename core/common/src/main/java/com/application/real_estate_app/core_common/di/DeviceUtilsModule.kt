package com.application.real_estate_app.core_common.di

import com.application.real_estate_app.core_common.interfaces.IDeviceUtils
import com.application.real_estate_app.core_common.system.DeviceUtils
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