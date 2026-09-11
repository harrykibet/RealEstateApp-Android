package com.estatia.realestate.apps.core.analytics.di

import com.estatia.realestate.apps.core.architecture.annotations.Logic.Utility
import com.estatia.realestate.apps.core.architecture.annotations.Logic.Utility
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.logging.LoggingMeterRegistry
import javax.inject.Singleton
import com.estatia.realestate.apps.core.architecture.annotations.Logic.Utility

@Module
@InstallIn(SingletonComponent::class)
@Utility
abstract class DemoAnalyticsModule {
    @Binds
    @Singleton
    abstract fun bindsAnalyticsHelper(analyticsHelperImpl: AnalyticsHelper): IAnalyticsHelper

    companion object {
        @Provides
        @IntoSet
        fun provideLoggingMeterRegistry(): MeterRegistry =
            LoggingMeterRegistry()
    }
}
