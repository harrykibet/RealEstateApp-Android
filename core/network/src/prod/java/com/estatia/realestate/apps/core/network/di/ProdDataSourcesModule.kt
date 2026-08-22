package com.estatia.realestate.apps.core.network.di

import com.estatia.realestate.apps.core.domain.analytics.*
import com.estatia.realestate.apps.core.domain.common.*
import com.estatia.realestate.apps.core.domain.config.*
import com.estatia.realestate.apps.core.domain.repository.*
import com.estatia.realestate.apps.core.domain.security.*
import com.estatia.realestate.apps.core.common.interfaces.IBackendInitializer
import com.estatia.realestate.apps.core.network.interfaces.*
import com.estatia.realestate.apps.core.network.sources.firebase.*
import com.estatia.realestate.apps.core.network.sources.aws.*
import com.estatia.realestate.apps.core.network.sources.SecretRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton


// Network EndPoints
@Module
@InstallIn(SingletonComponent::class)
internal object ProdDataSourcesModule {

    // --- Backend Initializers ---

    @Provides
    @IntoSet
    fun provideFirebaseInitializer(
        initializer: FirebaseBackendInitializer): IBackendInitializer = initializer

    @Provides
    @IntoSet
    fun provideAwsInitializer(
        initializer: AwsBackendInitializer): IBackendInitializer = initializer

    // --- Active Backend Bindings (AWS Primary) ---

    @Provides
    @Singleton
    fun provideSearchRemoteSource(
        dataSource: AwsSearchRemoteDataSource): ISearchRemoteDataSource = dataSource

    @Provides
    @Singleton
    fun providePropertyRemoteSource(
        dataSource: AwsPropertyRemoteDataSource
    ): IPropertyRemoteDatasource = dataSource

    @Provides
    @Singleton
    fun provideAuthRemoteSource(
        dataSource: AwsAuthService): IAuthRemoteDataSource = dataSource

    @Provides
    @Singleton
    fun provideCommentsRemoteSource(
        dataSource: AwsCommentsRemoteDataSource): ICommentsRemoteDataSource = dataSource

    @Provides
    @Singleton
    fun provideAnalyticsRemoteSource(
        dataSource: AwsAnalyticsRemoteDataSource): IAnalyticsRemoteDataSource = dataSource

    @Provides
    @Singleton
    fun provideUserRemoteDataSource(
        dataSource: AwsUserRemoteDataSource): IUserRemoteDataSource = dataSource

    @Provides
    @Singleton
    fun provideConfigRemoteDataSource(
        dataSource: AwsConfigRemoteDataSource): IConfigRemoteDataSource = dataSource

    @Provides
    @Singleton
    fun provideCrashReporter(
        reporter: AwsCrashReporter): ICrashReporter = reporter

    @Provides
    @Singleton
    fun providePaymentsRemoteSource(
        dataSource: AwsPaymentsRemoteDataSource): IPaymentsRemoteDataSource = dataSource

    // --- Shared / Infrastructure ---

    @Provides
    @Singleton
    fun provideSecretRemoteSource(
        dataSource: SecretRemoteDataSource): ISecretRemoteDataSource = dataSource


    // --- Firebase Bindings (Commented for future reference) ---
    /*
    @Provides
    @Singleton
    fun provideFirebaseSearchRemoteSource(
        dataSource: FirestoreSearch): ISearchRemoteDataSource = dataSource

    @Provides
    @Singleton
    fun provideFirebasePropertyRemoteSource(
        dataSource: FirestoreProperties): IPropertyRemoteDatasource = dataSource

    @Provides
    @Singleton
    fun provideFirebaseAuthRemoteSource(
        dataSource: FirebaseAuthService): IAuthRemoteDataSource = dataSource

    @Provides
    @Singleton
    fun provideFirebaseCommentsRemoteSource(
        dataSource: FirestoreComments): ICommentsRemoteDataSource = dataSource

    @Provides
    @Singleton
    fun provideFirebaseAnalyticsRemoteSource(
        dataSource: FirestoreAnalytics): IAnalyticsRemoteDataSource = dataSource

    @Provides
    @Singleton
    fun provideFirebaseUserRemoteDataSource(
        dataSource: FirestoreUsers): IUserRemoteDataSource = dataSource

    @Provides
    @Singleton
    fun provideFirebaseConfigRemoteDataSource(
        dataSource: FirebaseConfig): IConfigRemoteDataSource = dataSource

    @Provides
    @Singleton
    fun provideFirebaseCrashReporter(
        reporter: FirebaseCrashReporter): ICrashReporter = reporter

    @Provides
    @Singleton
    fun provideFirebasePaymentsRemoteSource(
        dataSource: FirebasePaymentsRemoteDataSource): IPaymentsRemoteDataSource = dataSource
    */
}
