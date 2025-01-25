package com.application.real_estate_app.feature_search.di

import com.application.real_estate_app.feature_search.data.sources.remote.RemoteDataSource
import com.application.real_estate_app.feature_search.domain.interfaces.IRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteSourceModule {

    // bind IFeatureSearchRepo to FeatureSearchRepo
    @Binds
    @Singleton
    abstract fun bindRemoteSource(dataSource: RemoteDataSource) : IRemoteDataSource
}