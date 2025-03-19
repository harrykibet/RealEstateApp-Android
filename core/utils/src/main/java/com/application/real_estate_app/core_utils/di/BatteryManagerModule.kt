package com.application.real_estate_app.core_utils.di

import com.application.real_estate_app.core_interface.IBatteryManager
import com.application.real_estate_app.core_utils.system.BatteryOptimizationManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BatteryManagerModule {
    @Binds
    @Singleton
    abstract fun bindBatteryManager(batteryManager: BatteryOptimizationManager) : IBatteryManager
}