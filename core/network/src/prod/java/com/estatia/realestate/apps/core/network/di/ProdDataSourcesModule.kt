package com.estatia.realestate.apps.core.network.di

import com.estatia.realestate.apps.core.network.interfaces.*
import com.estatia.realestate.apps.core.network.sources.*
import com.estatia.realestate.apps.core.network.sources.firebase.*
import com.estatia.realestate.apps.core.network.sources.aws.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton


// Network EndPoints
@Module
@InstallIn(SingletonComponent::class)
abstract class ProdDataSourcesModule {

    // --- Backend Initializers ---

    @Binds
    @IntoSet
    internal abstract fun bindFirebaseInitializer(
        initializer: FirebaseBackendInitializer): IBackendInitializer

    @Binds
    @IntoSet
    internal abstract fun bindAwsInitializer(
        initializer: AwsBackendInitializer): IBackendInitializer

    // --- Firebase Bindings (Current) ---

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

    @Binds
    @Singleton
    internal abstract fun bindPaymentsRemoteSource(
        dataSource: FirebasePaymentsRemoteDataSource): IPaymentsRemoteDataSource

    // --- AWS Bindings (Future/Alternative) ---
    /*
    @Binds
    @Singleton
    internal abstract fun bindAwsAuthRemoteSource(
        dataSource: AwsAuthService): IAuthRemoteDataSource

    @Binds
    @Singleton
    internal abstract fun bindAwsPaymentsRemoteSource(
        dataSource: AwsPaymentsRemoteDataSource): IPaymentsRemoteDataSource
    */
}
