package com.application.real_estate_app.feature_search.di

import com.application.real_estate_app.feature_search.data.sources.local.LocalDataSource
import com.application.real_estate_app.feature_search.domain.interfaces.ILocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalSourceModule {
    @Binds
    @Singleton
    abstract fun bindLocalSource(dataSource: LocalDataSource) : ILocalDataSource
}