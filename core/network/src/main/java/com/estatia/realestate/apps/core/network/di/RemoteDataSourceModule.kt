package com.estatia.realestate.apps.core.network.di

import com.estatia.realestate.apps.core.network.interfaces.IRemoteConfigManager
import com.estatia.realestate.apps.core.network.interfaces.IAnalyticsRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.IAuthRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.ICommentsRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.IGoogleCloudKmsManager
import com.estatia.realestate.apps.core.network.interfaces.IGoogleCloudSecretsManager
import com.estatia.realestate.apps.core.network.interfaces.IPropertyRemoteDatasource
import com.estatia.realestate.apps.core.network.interfaces.ISearchRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.IUserRemoteDataSource
import com.estatia.realestate.apps.core.network.sources.AnalyticsRemoteDataSource
import com.estatia.realestate.apps.core.network.sources.AuthRemoteDataSource
import com.estatia.realestate.apps.core.network.sources.CommentsRemoteDataSource
import com.estatia.realestate.apps.core.network.sources.GoogleCloudKmsManager
import com.estatia.realestate.apps.core.network.sources.GoogleCloudSecretsManager
import com.estatia.realestate.apps.core.network.sources.PropertyRemoteDataSource
import com.estatia.realestate.apps.core.network.sources.RemoteConfigManager
import com.estatia.realestate.apps.core.network.sources.SearchRemoteDataSource
import com.estatia.realestate.apps.core.network.sources.UserRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


// Network EndPoints
@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindSearchRemoteSource(dataSource: SearchRemoteDataSource) : ISearchRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindPropertyRemoteSource(dataSource: PropertyRemoteDataSource) : IPropertyRemoteDatasource

    @Binds
    @Singleton
    abstract fun bindAuthRemoteSource(dataSource: AuthRemoteDataSource) : IAuthRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindCommentsRemoteSource(dataSource: CommentsRemoteDataSource) : ICommentsRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindAnalyticsRemoteSource(dataSource: AnalyticsRemoteDataSource) : IAnalyticsRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindUserRemoteDataSource(dataSource : UserRemoteDataSource) : IUserRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindGoogleKmsManager(googleKmsManager: GoogleCloudKmsManager): IGoogleCloudKmsManager

    @Binds
    @Singleton
    abstract fun bindGoogleSecretsManager(googleSecretsManager: GoogleCloudSecretsManager): IGoogleCloudSecretsManager

    @Binds
    @Singleton
    abstract fun bindRemoteConfigManager(remoteConfigManager: RemoteConfigManager) : IRemoteConfigManager
}