package com.estatia.realestate.apps.core.network.di

import com.estatia.realestate.apps.core.network.interfaces.*
import com.estatia.realestate.apps.core.network.sources.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


// Network EndPoints
@Module
@InstallIn(SingletonComponent::class)
abstract class ProdDataSourcesModule {

    @Binds
    @Singleton
    internal abstract fun bindSearchRemoteSource(
        dataSource: FirestoreSearch): ISearchRemoteDataSource

    @Binds
    @Singleton
    internal abstract fun bindPropertyRemoteSource(
        dataSource: FirestoreProperties): IPropertyRemoteDatasource

    @Binds
    @Singleton
    internal abstract fun bindAuthRemoteSource(
        dataSource: FirebaseAuthService): IAuthRemoteDataSource

    @Binds
    @Singleton
    internal abstract fun bindCommentsRemoteSource(
        dataSource: FirestoreComments): ICommentsRemoteDataSource

    @Binds
    @Singleton
    internal abstract fun bindAnalyticsRemoteSource(
        dataSource: FirestoreAnalytics): IAnalyticsRemoteDataSource

    @Binds
    @Singleton
    internal abstract fun bindUserRemoteDataSource(
        dataSource: FirestoreUsers): IUserRemoteDataSource

    @Binds
    @Singleton
    internal abstract fun bindConfigRemoteDataSource(
        dataSource: FirebaseConfig): IConfigRemoteDataSource

    @Binds
    @Singleton
    internal abstract fun bindSecretRemoteSource(
        dataSource: SecretRemoteDataSource): ISecretRemoteDataSource
}
