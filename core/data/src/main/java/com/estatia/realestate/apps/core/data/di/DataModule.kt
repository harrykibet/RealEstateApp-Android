package com.estatia.realestate.apps.core.data.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.estatia.realestate.apps.core.data.interfaces.IAnalyticsRepository
import com.estatia.realestate.apps.core.data.interfaces.IAuthRepository
import com.estatia.realestate.apps.core.data.interfaces.ICommentsRepository
import com.estatia.realestate.apps.core.data.interfaces.ICryptoRepository
import com.estatia.realestate.apps.core.data.interfaces.IPropertyRepository
import com.estatia.realestate.apps.core.data.interfaces.ISearchRepository
import com.estatia.realestate.apps.core.data.interfaces.ISecurityRepository
import com.estatia.realestate.apps.core.data.interfaces.IUserRepository
import com.estatia.realestate.apps.core.data.repositories.AnalyticsRepository
import com.estatia.realestate.apps.core.data.repositories.AuthRepository
import com.estatia.realestate.apps.core.data.repositories.CommentsRepository
import com.estatia.realestate.apps.core.data.repositories.CryptoRepository
import com.estatia.realestate.apps.core.data.repositories.PropertyRepository
import com.estatia.realestate.apps.core.data.repositories.SearchRepository
import com.estatia.realestate.apps.core.data.repositories.SecurityRepository
import com.estatia.realestate.apps.core.data.repositories.UserRepository
import com.estatia.realestate.apps.core.data.util.ConnectivityManagerNetworkMonitor
import com.estatia.realestate.apps.core.data.util.NetworkMonitor
import com.estatia.realestate.apps.core.data.util.TimeZoneBroadcastMonitor
import com.estatia.realestate.apps.core.data.util.TimeZoneMonitor
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    companion object {
        @Provides
        @Singleton
        fun provideEncryptedPreferences(context: Context): SharedPreferences {
            return EncryptedSharedPreferences.create(
                context,
                "secure_prefs",
                MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }

    @Binds
    @Singleton
    abstract fun bindCryptoRepository(cryptoRepository: CryptoRepository) : ICryptoRepository

    @Binds
    @Singleton
    abstract fun bindAnalyticsRepository(analyticsRepository: AnalyticsRepository) : IAnalyticsRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(repository: AuthRepository) : IAuthRepository

    @Binds
    @Singleton
    abstract fun bindCommentsRepository(repository: CommentsRepository) : ICommentsRepository

    @Binds
    @Singleton
    abstract fun bindPropertyRepository(repository: PropertyRepository) : IPropertyRepository

    @Binds
    @Singleton
    abstract fun bindSearchRepo(repo: SearchRepository): ISearchRepository

    @Binds
    @Singleton
    abstract fun bindSecurityRepo(securityRepository: SecurityRepository) : ISecurityRepository

    @Binds
    @Singleton
    abstract fun bindUserRepo(repo: UserRepository): IUserRepository

    @Binds
    internal abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor

    @Binds
    internal abstract fun binds(impl: TimeZoneBroadcastMonitor): TimeZoneMonitor
}