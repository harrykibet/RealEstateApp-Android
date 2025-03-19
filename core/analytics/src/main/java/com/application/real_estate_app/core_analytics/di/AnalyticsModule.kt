package com.application.real_estate_app.core_analytics.di

import com.application.real_estate_app.core_analytics.data.repositories.AnalyticsRepository
import com.application.real_estate_app.core_analytics.domain.interfaces.IAnalyticsRepo
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
    abstract fun bindAnalyticsRepo(repo: AnalyticsRepository) : IAnalyticsRepo
}