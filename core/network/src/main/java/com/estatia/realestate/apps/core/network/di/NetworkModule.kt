package com.estatia.realestate.apps.core.network.di

import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.config.repository.ConfigRepository
import com.estatia.realestate.apps.core.network.core.AndroidNetworkStateProvider
import com.estatia.realestate.apps.core.network.core.FirebaseNetworkClient
import com.estatia.realestate.apps.core.network.error_mappers.NetworkErrorMapper
import com.estatia.realestate.apps.core.network.core.ExponentialRetryPolicy
import com.estatia.realestate.apps.core.network.error_mappers.ExceptionMapper
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.network.interfaces.IExceptionMapper
import com.estatia.realestate.apps.core.network.interfaces.IAuthExceptionMapper
import com.estatia.realestate.apps.core.network.interfaces.IFirebaseStorageErrorMapper
import com.estatia.realestate.apps.core.network.interfaces.IFirestoreErrorMapper
import com.estatia.realestate.apps.core.network.interfaces.INetworkErrorMapper
import com.estatia.realestate.apps.core.network.interfaces.INetworkStateProvider
import com.estatia.realestate.apps.core.network.interfaces.IRetryPolicy
import com.estatia.realestate.apps.core.network.error_mappers.*
import com.estatia.realestate.apps.core.network.interfaces.IFirebaseErrorMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Provides
    @Singleton
    fun provideNetworkStatsManager(@ApplicationContext context: Context): NetworkStatsManager {
        return context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
    }

    @Provides
    @Singleton
    fun provideRetryPolicy(): IRetryPolicy {
        return ExponentialRetryPolicy(
            exceptionMapper = ExceptionMapper(
                networkMapper = NetworkErrorMapper(),
                authMapper = FirebaseAuthErrorMapper(),
                databaseMapper = FirebaseFirestoreErrorMapper(),
                storageMapper = FirebaseStorageErrorMapper(),
                fallbackFirebaseMapper = FirebaseFallbackErrorMapper()
            )
        )
    }

    @Provides
    @Singleton
    fun provideNetworkErrorMapper(): INetworkErrorMapper {
        return NetworkErrorMapper()
    }

    @Provides
    @Singleton
    fun provideExceptionMapper(
        networkMapper: INetworkErrorMapper,
        authMapper: IAuthExceptionMapper,
        databaseMapper: IFirestoreErrorMapper,
        storageMapper: IFirebaseStorageErrorMapper,
        fallbackFirebaseMapper: IFirebaseErrorMapper
    ): IExceptionMapper {
        return ExceptionMapper(
            networkMapper,
            authMapper,
            databaseMapper,
            storageMapper,
            fallbackFirebaseMapper)
    }


    @Provides
    @Singleton
    fun provideNetworkClient(
        networkStateProvider: INetworkStateProvider,
        retryPolicy: IRetryPolicy,
        exceptionMapper: IExceptionMapper,
        logger: ILogger
    ): INetworkClient {
        return FirebaseNetworkClient(networkStateProvider, retryPolicy, exceptionMapper, logger)
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
    fun provideTelephonyManager(@ApplicationContext context: Context): TelephonyManager {
        return context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply
            { HttpLoggingInterceptor.Level.BODY })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        config: ConfigRepository
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
}