package com.application.real_estate_app.core_network.di

import com.application.real_estate_app.core_network.interfaces.IPropertyRemoteDatasource
import com.application.real_estate_app.core_network.interfaces.ISearchRemoteDataSource
import com.application.real_estate_app.core_network.sources.PropertyRemoteDataSource
import com.application.real_estate_app.core_network.sources.SearchRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteDataSourceModule {

    // bind IFeatureSearchRepo to FeatureSearchRepo
    @Binds
    @Singleton
    abstract fun bindSearchRemoteSource(dataSource: SearchRemoteDataSource) : ISearchRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindPropertyRemoteSource(dataSource: PropertyRemoteDataSource) : IPropertyRemoteDatasource
}