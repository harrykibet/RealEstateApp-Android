package com.estatia.realestate.apps.core.network.di

import com.estatia.realestate.apps.core.network.interfaces.*
import com.estatia.realestate.apps.core.network.sources.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DemoDataSourcesModule {

    @Binds
    @Singleton
    abstract fun bindSearchRemoteSource(
        dataSource: DemoSearchRemoteDataSource): ISearchRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindPropertyRemoteSource(
        dataSource: DemoPropertyRemoteDataSource): IPropertyRemoteDatasource

    @Binds
    @Singleton
    abstract fun bindAuthRemoteSource(
        dataSource: DemoAuthRemoteDataSource): IAuthRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindCommentsRemoteSource(
        dataSource: DemoCommentsRemoteDataSource): ICommentsRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindAnalyticsRemoteSource(
        dataSource: DemoAnalyticsRemoteDataSource): IAnalyticsRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindUserRemoteDataSource(
        dataSource: DemoUserRemoteDataSource): IUserRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindConfigRemoteDataSource(
        dataSource: DemoConfigRemoteDataSource): IConfigRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindSecretRemoteSource(
        dataSource: DemoSecretRemoteDataSource): ISecretRemoteDataSource
}
