package com.application.real_estate_app.feature_analytics.di

import com.application.real_estate_app.core.interfaces.AnalyticsApiInterface
import com.application.real_estate_app.feature_analytics.data.apis.AnalyticsApi
import com.application.real_estate_app.feature_analytics.domain.interfaces.IAnalyticsApi
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {
    @Binds
    @Singleton
    abstract fun bindAnalyticsApi(api: AnalyticsApi) : IAnalyticsApi
}