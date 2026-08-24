package com.estatia.realestate.apps.core.network.di

import android.content.Context
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.domain.config.INetworkConfig
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import com.estatia.realestate.apps.core.network.api.SecretApi
import com.estatia.realestate.apps.core.network.core.AndroidNetworkStateProvider
import com.estatia.realestate.apps.core.network.core.ExponentialRetryPolicy
import com.estatia.realestate.apps.core.network.core.ProductionNetworkClient
import com.estatia.realestate.apps.core.network.error_mappers.ExceptionMapper
import com.estatia.realestate.apps.core.network.error_mappers.NetworkErrorMapper
import com.estatia.realestate.apps.core.network.interceptors.TracingInterceptor
import com.estatia.realestate.apps.core.network.interfaces.IAuthExceptionMapper
import com.estatia.realestate.apps.core.network.interfaces.IDatabaseErrorMapper
import com.estatia.realestate.apps.core.network.interfaces.IExceptionMapper
import com.estatia.realestate.apps.core.network.interfaces.IInfrastructureErrorMapper
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.network.interfaces.INetworkErrorMapper
import com.estatia.realestate.apps.core.network.interfaces.INetworkStateProvider
import com.estatia.realestate.apps.core.network.interfaces.IRetryPolicy
import com.estatia.realestate.apps.core.network.interfaces.IStorageErrorMapper
import com.estatia.realestate.apps.core.network.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
private annotation class BaseClient

@Module
@InstallIn(SingletonComponent::class)
object ProdNetworkModule {

    @Provides
    @Singleton
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Provides
    @Singleton
    fun provideTelephonyManager(@ApplicationContext context: Context): TelephonyManager {
        return context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    @Provides
    @Singleton
    fun provideNetworkStateProvider(
        connectivityManager: ConnectivityManager
    ): INetworkStateProvider {
        return AndroidNetworkStateProvider(connectivityManager)
    }

    @Provides
    @Singleton
    fun provideConnectionPool(): ConnectionPool {
        return ConnectionPool(
            maxIdleConnections = 20,
            keepAliveDuration = 5,
            timeUnit = TimeUnit.MINUTES
        )
    }

    @Provides
    @Singleton
    @BaseClient
    fun provideBaseOkHttpClient(
        connectionPool: ConnectionPool,
        tracingInterceptor: TracingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectionPool(connectionPool)
            .addInterceptor(tracingInterceptor)
            .protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        }
                    )
                }
            }
            .build()
    }

    @Provides
    @Singleton
    @AuthClient
    fun provideAuthOkHttpClient(
        @BaseClient baseClient: OkHttpClient
    ): OkHttpClient {
        return baseClient.newBuilder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @UploadClient
    fun provideUploadOkHttpClient(
        @BaseClient baseClient: OkHttpClient
    ): OkHttpClient {
        return baseClient.newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @PlaybackClient
    fun providePlaybackOkHttpClient(
        @BaseClient baseClient: OkHttpClient
    ): OkHttpClient {
        return baseClient.newBuilder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @BaseClient baseClient: OkHttpClient
    ): OkHttpClient {
        return baseClient.newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        @AuthClient okHttpClient: OkHttpClient,
        config: INetworkConfig
    ): Retrofit {

        val baseUrl = config.baseUrl
            .takeIf { it.endsWith("/") }
            ?: "${config.baseUrl}/"

        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideSecretApi(retrofit: Retrofit): SecretApi {
        return retrofit.create(SecretApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNetworkClient(
        retryPolicy: IRetryPolicy,
        exceptionMapper: IExceptionMapper,
        metricsTracker: IMetricsTracker,
        logger: ILogger
    ): INetworkClient {
        return ProductionNetworkClient(retryPolicy, exceptionMapper, metricsTracker, logger)
    }

    @Provides
    @Singleton
    fun provideRetryPolicy(
        exceptionMapper: IExceptionMapper
    ): IRetryPolicy {
        return ExponentialRetryPolicy(exceptionMapper)
    }

    @Provides
    @Singleton
    fun provideExceptionMapper(
        networkMapper: INetworkErrorMapper,
        @FirebaseMapper firebaseAuthMapper: IAuthExceptionMapper,
        @AwsMapper awsAuthMapper: IAuthExceptionMapper,
        @FirebaseMapper firebaseDatabaseMapper: IDatabaseErrorMapper,
        @AwsMapper awsDatabaseMapper: IDatabaseErrorMapper,
        @FirebaseMapper firebaseStorageMapper: IStorageErrorMapper,
        @AwsMapper awsStorageMapper: IStorageErrorMapper,
        @FirebaseMapper firebaseInfraMapper: IInfrastructureErrorMapper,
        @AwsMapper awsInfraMapper: IInfrastructureErrorMapper
    ): IExceptionMapper {
        return ExceptionMapper(
            networkMapper,
            firebaseAuthMapper,
            awsAuthMapper,
            firebaseDatabaseMapper,
            awsDatabaseMapper,
            firebaseStorageMapper,
            awsStorageMapper,
            firebaseInfraMapper,
            awsInfraMapper
        )
    }

    @Provides
    @Singleton
    fun provideNetworkErrorMapper(): INetworkErrorMapper {
        return NetworkErrorMapper()
    }
}
