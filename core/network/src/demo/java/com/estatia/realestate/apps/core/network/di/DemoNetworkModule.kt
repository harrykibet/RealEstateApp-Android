package com.estatia.realestate.apps.core.network.di

import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.AuthException
import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.common.exceptions.NetworkException
import com.estatia.realestate.apps.core.common.exceptions.StorageException
import com.estatia.realestate.apps.core.network.api.SecretApi
import com.estatia.realestate.apps.core.network.core.NetworkState
import com.estatia.realestate.apps.core.network.core.RetryConfig
import com.estatia.realestate.apps.core.network.di.NetworkInterceptors
import com.estatia.realestate.apps.core.network.error_mappers.NetworkErrorMapper
import com.estatia.realestate.apps.core.network.interfaces.IAuthExceptionMapper
import com.estatia.realestate.apps.core.network.interfaces.IExceptionMapper
import com.estatia.realestate.apps.core.network.interfaces.IInfrastructureErrorMapper
import com.estatia.realestate.apps.core.network.interfaces.IDatabaseErrorMapper
import com.estatia.realestate.apps.core.network.interfaces.IStorageErrorMapper
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.network.interfaces.INetworkErrorMapper
import com.estatia.realestate.apps.core.network.interfaces.INetworkStateProvider
import com.estatia.realestate.apps.core.network.interfaces.IRetryPolicy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DemoNetworkModule {

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
    fun provideTelephonyManager(@ApplicationContext context: Context): TelephonyManager {
        return context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    @Provides
    @Singleton
    fun provideNetworkStateProvider(): INetworkStateProvider {
        return object : INetworkStateProvider {
            override fun observe(): Flow<NetworkState> = flowOf(NetworkState.Connected)
            override fun current(): NetworkState = NetworkState.Connected
        }
    }

    @Provides
    @Singleton
    fun provideNetworkClient(): INetworkClient {
        return object : INetworkClient {
            override suspend fun <T> execute(config: RetryConfig?, apiCall: suspend () -> T): AppResult<T> {
                return try {
                    AppResult.Success(apiCall())
                } catch (e: Exception) {
                    AppResult.Error(NetworkException.Unknown(e))
                }
            }
        }
    }

    @Provides
    @Singleton
    fun provideExceptionMapper(): IExceptionMapper {
        return object : IExceptionMapper {
            override fun map(throwable: Throwable): AppException = NetworkException.Unknown(throwable)
        }
    }

    @Provides
    @Singleton
    fun provideNetworkErrorMapper(): INetworkErrorMapper = NetworkErrorMapper()

    @Provides
    @Singleton
    fun provideAuthExceptionMapper(): IAuthExceptionMapper = object : IAuthExceptionMapper {
        override fun map(throwable: Throwable): AuthException = AuthException.Unknown(throwable)
    }

    @Provides
    @Singleton
    fun provideDatabaseErrorMapper(): IDatabaseErrorMapper = object : IDatabaseErrorMapper {
        override fun map(throwable: Throwable): DatabaseException = DatabaseException.Unknown(throwable)
    }

    @Provides
    @Singleton
    fun provideStorageErrorMapper(): IStorageErrorMapper = object : IStorageErrorMapper {
        override fun map(throwable: Throwable): StorageException = StorageException.Unknown(throwable)
    }

    @Provides
    @Singleton
    fun provideInfrastructureErrorMapper(): IInfrastructureErrorMapper = object : IInfrastructureErrorMapper {
        override fun map(throwable: Throwable): AppException = NetworkException.Unknown(throwable)
    }
    
    @Provides
    @Singleton
    fun provideRetryPolicy(): IRetryPolicy = object : IRetryPolicy {
        override suspend fun <T> execute(config: RetryConfig?, block: suspend () -> T): T = block()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @NetworkInterceptors interceptors: Set<@JvmSuppressWildcards Interceptor>
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .apply {
                interceptors.forEach { addInterceptor(it) }
            }
            .build()
    }

    @Provides
    @Singleton
    @AuthClient
    fun provideAuthOkHttpClient(client: OkHttpClient): OkHttpClient = client

    @Provides
    @Singleton
    @UploadClient
    fun provideUploadOkHttpClient(client: OkHttpClient): OkHttpClient = client

    @Provides
    @Singleton
    @PlaybackClient
    fun providePlaybackOkHttpClient(client: OkHttpClient): OkHttpClient = client

    @Provides
    @Singleton
    fun provideRetrofit(@AuthClient okHttpClient: OkHttpClient): Retrofit {
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
        return Retrofit.Builder()
            .baseUrl("https://demo.estatia.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideSecretApi(retrofit: Retrofit): SecretApi {
        return retrofit.create(SecretApi::class.java)
    }
}
