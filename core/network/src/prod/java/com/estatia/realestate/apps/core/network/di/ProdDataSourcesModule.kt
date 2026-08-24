package com.estatia.realestate.apps.core.network.di

import com.estatia.realestate.apps.core.common.interfaces.IBackendInitializer
import com.estatia.realestate.apps.core.network.interfaces.*
import com.estatia.realestate.apps.core.network.sources.firebase.*
import com.estatia.realestate.apps.core.network.sources.aws.*
import com.estatia.realestate.apps.core.network.sources.SecretRemoteDataSource
import com.estatia.realestate.apps.core.domain.analytics.ICrashReporter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

/**
 * Module providing remote data source implementations for the production environment.
 * Uses AWS Amplify and Pinpoint as the primary infrastructure.
 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class ProdDataSourcesModule {

    // --- Backend Initializers ---

    @Binds
    @IntoSet
    abstract fun bindFirebaseInitializer(initializer: FirebaseBackendInitializer): IBackendInitializer

    @Binds
    @IntoSet
    abstract fun bindAwsInitializer(initializer: AwsBackendInitializer): IBackendInitializer

    // --- Active Backend Bindings (AWS Primary) ---

    @Binds
    @Singleton
    abstract fun bindSearchRemoteSource(dataSource: AwsSearchRemoteDataSource): ISearchRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindPropertyRemoteSource(dataSource: AwsPropertyRemoteDataSource): IPropertyRemoteDatasource

    @Binds
    @Singleton
    abstract fun bindAuthRemoteSource(dataSource: AwsAuthService): IAuthRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindCommentsRemoteSource(dataSource: AwsCommentsRemoteDataSource): ICommentsRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindAnalyticsRemoteSource(dataSource: AwsAnalyticsRemoteDataSource): IAnalyticsRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindUserRemoteDataSource(dataSource: AwsUserRemoteDataSource): IUserRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindConfigRemoteDataSource(dataSource: AwsConfigRemoteDataSource): IConfigRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindCrashReporter(reporter: AwsCrashReporter): ICrashReporter

    @Binds
    @Singleton
    abstract fun bindPaymentsRemoteSource(dataSource: AwsPaymentsRemoteDataSource): IPaymentsRemoteDataSource

    // --- Shared / Infrastructure ---

    @Binds
    @Singleton
    abstract fun bindSecretRemoteSource(dataSource: SecretRemoteDataSource): ISecretRemoteDataSource

    // --- Firebase Bindings (Disabled) ---
    /*
    @Binds
    @Singleton
    abstract fun bindFirebaseSearchRemoteSource(dataSource: FirestoreSearch): ISearchRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindFirebasePropertyRemoteSource(dataSource: FirestoreProperties): IPropertyRemoteDatasource

    @Binds
    @Singleton
    abstract fun bindFirebaseCommentsRemoteSource(dataSource: FirestoreComments): ICommentsRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindFirebaseAnalyticsRemoteSource(dataSource: FirestoreAnalytics): IAnalyticsRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindFirebaseUserRemoteDataSource(dataSource: FirestoreUsers): IUserRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindFirebaseConfigRemoteDataSource(dataSource: FirebaseConfig): IConfigRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindFirebaseCrashReporter(reporter: FirebaseCrashReporter): ICrashReporter

    @Binds
    @Singleton
    abstract fun bindFirebasePaymentsRemoteSource(dataSource: FirebasePaymentsRemoteDataSource): IPaymentsRemoteDataSource
    */
}
