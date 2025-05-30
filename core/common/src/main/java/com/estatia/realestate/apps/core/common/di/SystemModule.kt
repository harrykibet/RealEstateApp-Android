package com.estatia.realestate.apps.core.common.di

import android.os.PowerManager
import dagger.hilt.components.SingletonComponent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton
import android.content.Context
import android.hardware.display.DisplayManager

@Module
@InstallIn(SingletonComponent::class)
object SystemModule {

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
}
