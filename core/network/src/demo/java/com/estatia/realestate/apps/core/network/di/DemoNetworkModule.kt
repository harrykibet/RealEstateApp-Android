package com.estatia.realestate.apps.core.network.di

import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import com.estatia.realestate.apps.core.common.exceptions.*
import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.domain.interfaces.IConfigProvider
import com.estatia.realestate.apps.core.network.api.SecretApi
import com.estatia.realestate.apps.core.network.core.NetworkState
import com.estatia.realestate.apps.core.network.core.RetryConfig
import com.estatia.realestate.apps.core.network.error_mappers.NetworkErrorMapper
import com.estatia.realestate.apps.core.network.interfaces.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
    fun provideFirestoreErrorMapper(): IFirestoreErrorMapper = object : IFirestoreErrorMapper {
        override fun map(throwable: Throwable): DatabaseException = DatabaseException.Unknown(throwable)
    }

    @Provides
    @Singleton
    fun provideFirebaseStorageErrorMapper(): IFirebaseStorageErrorMapper = object : IFirebaseStorageErrorMapper {
        override fun map(throwable: Throwable): StorageException = StorageException.Unknown(throwable)
    }

    @Provides
    @Singleton
    fun provideFirebaseErrorMapper(): IFirebaseErrorMapper = object : IFirebaseErrorMapper {
        override fun map(throwable: com.google.firebase.FirebaseException): AppException = NetworkException.Unknown(throwable)
    }
    
    @Provides
    @Singleton
    fun provideRetryPolicy(): IRetryPolicy = object : IRetryPolicy {
        override suspend fun <T> execute(config: RetryConfig?, block: suspend () -> T): T = block()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://demo.estatia.com/")
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
