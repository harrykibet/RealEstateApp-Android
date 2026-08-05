package com.estatia.realestate.apps.core.analytics.di

import com.estatia.realestate.apps.core.analytics.IAnalyticsHelper
import com.estatia.realestate.apps.core.analytics.AnalyticsHelper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DemoAnalyticsModule {
    @Binds
    @Singleton
    abstract fun bindsAnalyticsHelper(analyticsHelperImpl: AnalyticsHelper): IAnalyticsHelper
}
