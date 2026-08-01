package com.estatia.realestate.apps.core.database.di

import com.estatia.realestate.apps.core.database.core.LocalDatabaseExecutor
import com.estatia.realestate.apps.core.database.interfaces.ILocalDatabaseExecutor
import com.estatia.realestate.apps.core.database.sources.PropertyLocalDataSource
import com.estatia.realestate.apps.core.database.interfaces.IPropertyLocalDataSource
import com.estatia.realestate.apps.core.database.interfaces.ISearchLocalDataSource
import com.estatia.realestate.apps.core.database.sources.SearchLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SourcesModule {

    @Binds
    @Singleton
    abstract fun bindPropertyLocalSource(dataSource: PropertyLocalDataSource) : IPropertyLocalDataSource

    @Binds
    @Singleton
    abstract fun bindSearchLocalSource(dataSource: SearchLocalDataSource) : ISearchLocalDataSource

    @Binds
    @Singleton
    abstract fun bindLocalDatabaseExecutor(executor: LocalDatabaseExecutor) : ILocalDatabaseExecutor
}
