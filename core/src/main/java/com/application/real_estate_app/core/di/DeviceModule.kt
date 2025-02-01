package com.application.real_estate_app.core.di

import android.content.Context
import android.hardware.display.DisplayManager
import com.application.real_estate_app.core.utils.system.DeviceUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// core/di/DeviceModule.kt
@Module
@InstallIn(SingletonComponent::class)
object DeviceModule {
    @Provides
    @Singleton
    fun provideDeviceUtils(
        context: Context,
        displayManager: DisplayManager
    ): DeviceUtils = DeviceUtils(context, displayManager)
}