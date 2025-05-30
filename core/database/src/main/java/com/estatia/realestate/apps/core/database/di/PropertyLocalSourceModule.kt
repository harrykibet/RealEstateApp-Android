package com.estatia.realestate.apps.core.database.di

import com.estatia.realestate.apps.core.database.sources.PropertyLocalDataSource
import com.estatia.realestate.apps.core.database.interfaces.IPropertyLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PropertyLocalSourceModule {
    @Binds
    @Singleton
    abstract fun bindLocalSource(dataSource: PropertyLocalDataSource) : IPropertyLocalDataSource
}