package com.estatia.realestate.apps.core.network.di

import com.estatia.realestate.apps.core.domain.interfaces.IConfigProvider
import com.estatia.realestate.apps.core.network.api.SecretApi
import com.estatia.realestate.apps.core.network.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
        connectionPool: ConnectionPool
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectionPool(connectionPool)
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
        config: IConfigProvider
    ): Retrofit {

        val baseUrl = config.baseUrl
            .takeIf { it.endsWith("/") }
            ?: "${config.baseUrl}/"

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideSecretApi(retrofit: Retrofit): SecretApi {
        return retrofit.create(SecretApi::class.java)
    }
}
