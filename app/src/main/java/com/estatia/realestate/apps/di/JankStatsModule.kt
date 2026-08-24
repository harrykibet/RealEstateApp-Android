package com.estatia.realestate.apps.di

import android.app.Activity
import android.view.Window
import androidx.metrics.performance.JankStats
import androidx.metrics.performance.JankStats.OnFrameListener
import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.common.system.PerformanceMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
object JankStatsModule {
    @Provides
    fun providesOnFrameListener(
        performanceMonitor: PerformanceMonitor,
        logger: ILogger
    ): OnFrameListener = OnFrameListener { frameData ->
        performanceMonitor.reportFrameData(frameData.isJank)
        
        // Make sure to only log janky frames.
        if (frameData.isJank) {
            // We're currently logging this but would better report it to a backend.
            logger.d("Estatia Jank", frameData.toString())
        }
    }

    @Provides
    fun providesWindow(activity: Activity): Window = activity.window

    @Provides
    fun providesJankStats(
        window: Window,
        frameListener: OnFrameListener,
    ): JankStats = JankStats.createAndTrack(window, frameListener)
}
