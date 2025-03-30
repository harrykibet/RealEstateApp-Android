package com.application.real_estate_app.core_network.di

import com.application.real_estate_app.core_network.interfaces.IRemoteConfigManager
import com.application.real_estate_app.core_network.interfaces.IAnalyticsRemoteDataSource
import com.application.real_estate_app.core_network.interfaces.IAuthRemoteDataSource
import com.application.real_estate_app.core_network.interfaces.ICommentsRemoteDataSource
import com.application.real_estate_app.core_network.interfaces.IGoogleCloudKmsManager
import com.application.real_estate_app.core_network.interfaces.IGoogleCloudSecretsManager
import com.application.real_estate_app.core_network.interfaces.IPropertyRemoteDatasource
import com.application.real_estate_app.core_network.interfaces.ISearchRemoteDataSource
import com.application.real_estate_app.core_network.interfaces.IUserRemoteDataSource
import com.application.real_estate_app.core_network.sources.AnalyticsRemoteDataSource
import com.application.real_estate_app.core_network.sources.AuthRemoteDataSource
import com.application.real_estate_app.core_network.sources.CommentsRemoteDataSource
import com.application.real_estate_app.core_network.sources.GoogleCloudKmsManager
import com.application.real_estate_app.core_network.sources.GoogleCloudSecretsManager
import com.application.real_estate_app.core_network.sources.PropertyRemoteDataSource
import com.application.real_estate_app.core_network.sources.RemoteConfigManager
import com.application.real_estate_app.core_network.sources.SearchRemoteDataSource
import com.application.real_estate_app.core_network.sources.UserRemoteDataSource
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