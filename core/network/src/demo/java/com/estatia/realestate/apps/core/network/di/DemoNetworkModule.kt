package com.estatia.realestate.apps.core.network.di

import android.content.Context
import android.net.ConnectivityManager
import com.estatia.realestate.apps.core.common.exceptions.*
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
}
