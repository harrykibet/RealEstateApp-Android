package com.estatia.realestate.apps.core.network.di

import com.estatia.realestate.apps.core.network.interfaces.IAnalyticsRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.IAuthRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.ICommentsRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.IGoogleCloudKmsManager
import com.estatia.realestate.apps.core.network.interfaces.IGoogleCloudSecretsManager
import com.estatia.realestate.apps.core.network.interfaces.IPropertyRemoteDatasource
import com.estatia.realestate.apps.core.network.interfaces.ISearchRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.IUserRemoteDataSource
import com.estatia.realestate.apps.core.network.sources.FirebaseAnalytics
import com.estatia.realestate.apps.core.network.sources.FirebaseAuth
import com.estatia.realestate.apps.core.network.sources.FirestoreComments
import com.estatia.realestate.apps.core.network.sources.GoogleCloudKmsManager
import com.estatia.realestate.apps.core.network.sources.GoogleCloudSecretsManager
import com.estatia.realestate.apps.core.network.sources.FirestoreProperties
import com.estatia.realestate.apps.core.network.sources.FirestoreSearch
import com.estatia.realestate.apps.core.network.sources.FirestoreUsers
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


// Network EndPoints
@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourcesModule {

    @Binds
    @Singleton
    abstract fun bindSearchRemoteSource(
        dataSource: FirestoreSearch): ISearchRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindPropertyRemoteSource(
        dataSource: FirestoreProperties): IPropertyRemoteDatasource

    @Binds
    @Singleton
    abstract fun bindAuthRemoteSource(
        dataSource: FirebaseAuth): IAuthRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindCommentsRemoteSource(
        dataSource: FirestoreComments): ICommentsRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindAnalyticsRemoteSource(
        dataSource: FirebaseAnalytics): IAnalyticsRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindUserRemoteDataSource(
        dataSource: FirestoreUsers): IUserRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindGoogleKmsManager(
        googleKmsManager: GoogleCloudKmsManager): IGoogleCloudKmsManager

    @Binds
    @Singleton
    abstract fun bindGoogleSecretsManager(
        googleSecretsManager: GoogleCloudSecretsManager): IGoogleCloudSecretsManager
}