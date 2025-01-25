package com.application.real_estate_app.feature_analytics.di

import com.application.real_estate_app.core.domain.interfaces.AnalyticsApiInterface
import com.application.real_estate_app.feature_analytics.data.services.ImplAnalyticsCore
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
    abstract fun bindAnalyticsCore(analyticsCoreImpl: ImplAnalyticsCore): AnalyticsApiInterface
}