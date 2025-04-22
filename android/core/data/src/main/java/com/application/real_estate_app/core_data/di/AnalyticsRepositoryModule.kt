package com.application.real_estate_app.core_data.di

import com.application.real_estate_app.core_data.interfaces.IAnalyticsRepository
import com.application.real_estate_app.core_data.repositories.AnalyticsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAnalyticsRepository(repository: AnalyticsRepository) : IAnalyticsRepository
}