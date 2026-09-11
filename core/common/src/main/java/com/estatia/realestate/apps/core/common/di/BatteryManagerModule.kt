package com.estatia.realestate.apps.core.common.di

import com.estatia.realestate.apps.core.common.interfaces.IBatteryManager
import com.estatia.realestate.apps.core.common.system.BatteryOptimizationManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.estatia.realestate.apps.core.architecture.annotations.Utility

@Module
@InstallIn(SingletonComponent::class)
@Utility
abstract class BatteryManagerModule {
    @Binds
    @Singleton
    abstract fun bindBatteryManager(batteryManager: BatteryOptimizationManager) : IBatteryManager
}
