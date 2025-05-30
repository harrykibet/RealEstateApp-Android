package com.estatia.realestate.apps.core.database.di

import com.estatia.realestate.apps.core.database.sources.SearchLocalDataSource
import com.estatia.realestate.apps.core.database.interfaces.ISearchLocalDataSource
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