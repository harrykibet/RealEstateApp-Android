package com.application.real_estate_app.core_data.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.application.real_estate_app.core_data.interfaces.IAnalyticsRepository
import com.application.real_estate_app.core_data.interfaces.IAuthRepository
import com.application.real_estate_app.core_data.interfaces.ICommentsRepository
import com.application.real_estate_app.core_data.interfaces.ICryptoRepository
import com.application.real_estate_app.core_data.interfaces.IPropertyRepository
import com.application.real_estate_app.core_data.interfaces.ISearchRepository
import com.application.real_estate_app.core_data.interfaces.ISecurityRepository
import com.application.real_estate_app.core_data.interfaces.IUserRepository
import com.application.real_estate_app.core_data.repositories.AnalyticsRepository
import com.application.real_estate_app.core_data.repositories.AuthRepository
import com.application.real_estate_app.core_data.repositories.CommentsRepository
import com.application.real_estate_app.core_data.repositories.CryptoRepository
import com.application.real_estate_app.core_data.repositories.PropertyRepository
import com.application.real_estate_app.core_data.repositories.SearchRepository
import com.application.real_estate_app.core_data.repositories.SecurityRepository
import com.application.real_estate_app.core_data.repositories.UserRepository
import com.application.real_estate_app.core_data.util.ConnectivityManagerNetworkMonitor
import com.application.real_estate_app.core_data.util.NetworkMonitor
import com.application.real_estate_app.core_data.util.TimeZoneBroadcastMonitor
import com.application.real_estate_app.core_data.util.TimeZoneMonitor
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