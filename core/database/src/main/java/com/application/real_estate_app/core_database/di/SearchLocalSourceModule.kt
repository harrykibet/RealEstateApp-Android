package com.application.real_estate_app.core_database.di

import com.application.real_estate_app.core_database.sources.SearchLocalDataSource
import com.application.real_estate_app.core_database.interfaces.ISearchLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchLocalSourceModule {
    @Binds
    @Singleton
    abstract fun bindLocalSource(dataSource: SearchLocalDataSource) : ISearchLocalDataSource
}