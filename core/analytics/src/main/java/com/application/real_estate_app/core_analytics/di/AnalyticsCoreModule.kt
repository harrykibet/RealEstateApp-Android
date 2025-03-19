package com.application.real_estate_app.core_analytics.di

import com.application.real_estate_app.core_analytics.data.services.ImplAnalyticsCore
import com.application.real_estate_app.core_interface.AnalyticsRepoInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsCoreModule {
    @Binds
    @Singleton
    abstract fun bindAnalyticsCore(analyticsCoreImpl: ImplAnalyticsCore): AnalyticsRepoInterface
}