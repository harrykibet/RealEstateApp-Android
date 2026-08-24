package com.estatia.realestate.apps.core.network.di

import com.estatia.realestate.apps.core.network.interfaces.IAnalyticsRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.IAuthRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.ICommentsRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.IConfigRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.IPaymentsRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.IPropertyRemoteDatasource
import com.estatia.realestate.apps.core.network.interfaces.ISearchRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.ISecretRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.IUserRemoteDataSource
import com.estatia.realestate.apps.core.common.interfaces.IBackendInitializer
import com.estatia.realestate.apps.core.network.sources.DemoAnalyticsRemoteDataSource
import com.estatia.realestate.apps.core.network.sources.DemoAuthRemoteDataSource
import com.estatia.realestate.apps.core.network.sources.DemoCommentsRemoteDataSource
import com.estatia.realestate.apps.core.network.sources.DemoConfigRemoteDataSource
import com.estatia.realestate.apps.core.network.sources.DemoPaymentsRemoteDataSource
import com.estatia.realestate.apps.core.network.sources.DemoPropertyRemoteDataSource
import com.estatia.realestate.apps.core.network.sources.DemoSearchRemoteDataSource
import com.estatia.realestate.apps.core.network.sources.DemoSecretRemoteDataSource
import com.estatia.realestate.apps.core.network.sources.DemoUserRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DemoDataSourcesModule {

    @Binds
    @Singleton
    internal abstract fun bindSearchRemoteSource(
        dataSource: DemoSearchRemoteDataSource): ISearchRemoteDataSource

    @Binds
    @Singleton
    internal abstract fun bindPropertyRemoteSource(
        dataSource: DemoPropertyRemoteDataSource): IPropertyRemoteDatasource

    @Binds
    @Singleton
    internal abstract fun bindAuthRemoteSource(
        dataSource: DemoAuthRemoteDataSource): IAuthRemoteDataSource

    @Binds
    @Singleton
    internal abstract fun bindCommentsRemoteSource(
        dataSource: DemoCommentsRemoteDataSource): ICommentsRemoteDataSource

    @Binds
    @Singleton
    internal abstract fun bindAnalyticsRemoteSource(
        dataSource: DemoAnalyticsRemoteDataSource): IAnalyticsRemoteDataSource

    @Binds
    @Singleton
    internal abstract fun bindUserRemoteDataSource(
        dataSource: DemoUserRemoteDataSource): IUserRemoteDataSource

    @Binds
    @Singleton
    internal abstract fun bindConfigRemoteDataSource(
        dataSource: DemoConfigRemoteDataSource): IConfigRemoteDataSource

    @Binds
    @Singleton
    internal abstract fun bindSecretRemoteSource(
        dataSource: DemoSecretRemoteDataSource): ISecretRemoteDataSource

    @Binds
    @Singleton
    internal abstract fun bindPaymentsRemoteSource(
        dataSource: DemoPaymentsRemoteDataSource): IPaymentsRemoteDataSource

    companion object {

        @Provides
        @Singleton
        fun provideCrashReporter(): com.estatia.realestate.apps.core.domain.analytics.ICrashReporter = object : com.estatia.realestate.apps.core.domain.analytics.ICrashReporter {
            override fun log(message: String) {}
            override fun recordException(throwable: Throwable) {}
            override fun setCustomKey(key: String, value: String) {}
        }
    }
}
