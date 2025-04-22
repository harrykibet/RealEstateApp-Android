package com.application.real_estate_app.core_database.di

import com.application.real_estate_app.core_database.sources.PropertyLocalDataSource
import com.application.real_estate_app.core_database.interfaces.IPropertyLocalDataSource
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