package com.estatia.realestate.apps.core.common.di

import android.content.Context
import android.hardware.display.DisplayManager
import android.os.PowerManager
import com.estatia.realestate.apps.core.common.interfaces.BuildEnvironment
import com.estatia.realestate.apps.core.common.interfaces.IClock
import com.estatia.realestate.apps.core.common.interfaces.SystemClock
import com.estatia.realestate.apps.core.common.system.AndroidBuildEnvironment
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SystemModule {

    @Binds
    @Singleton
    abstract fun bindBuildEnvironment(
        buildEnvironment: AndroidBuildEnvironment
    ): BuildEnvironment

    companion object {
        @Provides
        @Singleton
        fun providePowerManager(@ApplicationContext context: Context): PowerManager {
            return context.getSystemService(Context.POWER_SERVICE) as PowerManager
        }

        @Provides
        @Singleton
        fun provideDisplayManager(@ApplicationContext context: Context): DisplayManager {
            return context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        }

        @Provides
        @Singleton
        fun provideClock(): IClock = SystemClock()
    }
}
