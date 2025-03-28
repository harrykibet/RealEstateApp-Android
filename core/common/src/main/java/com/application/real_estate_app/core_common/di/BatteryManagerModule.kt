package com.application.real_estate_app.core_common.di

import com.application.real_estate_app.core_common.interfaces.IBatteryManager
import com.application.real_estate_app.core_common.system.BatteryOptimizationManager
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