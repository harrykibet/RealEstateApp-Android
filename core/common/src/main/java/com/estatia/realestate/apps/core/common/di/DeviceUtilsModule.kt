package com.estatia.realestate.apps.core.common.di

import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import com.estatia.realestate.apps.core.common.system.DeviceUtils
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