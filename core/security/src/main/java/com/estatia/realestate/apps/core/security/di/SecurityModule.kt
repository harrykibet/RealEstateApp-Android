package com.estatia.realestate.apps.core.security.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.estatia.realestate.apps.core.common.di.ApplicationScope
import com.estatia.realestate.apps.core.common.system.Dispatcher
import com.estatia.realestate.apps.core.common.system.EstatiaDispatchers
import com.estatia.realestate.apps.core.security.BuildConfigApiKeyProvider
import com.estatia.realestate.apps.core.security.TokenLocalDataSource
import com.estatia.realestate.apps.core.security.crypto.AesGcmCryptoEngine
import com.estatia.realestate.apps.core.security.crypto.RsaCryptoEngine
import com.estatia.realestate.apps.core.security.crypto.SignatureManager
import com.estatia.realestate.apps.core.security.interfaces.ApiKeyProvider
import com.estatia.realestate.apps.core.security.interfaces.IAesGcmCryptoEngine
import com.estatia.realestate.apps.core.security.interfaces.IKeyStoreManager
import com.estatia.realestate.apps.core.security.interfaces.IRsaCryptoEngine
import com.estatia.realestate.apps.core.security.interfaces.ISignatureManager
import com.estatia.realestate.apps.core.security.interfaces.ITokenLocalDataSource
import com.estatia.realestate.apps.core.security.interfaces.ISecurityExceptionTranslator
import com.estatia.realestate.apps.core.security.interfaces.ICryptoExecutor
import com.estatia.realestate.apps.core.security.interfaces.IHashManager
import com.estatia.realestate.apps.core.security.core.CryptoExecutor
import com.estatia.realestate.apps.core.security.crypto.HashManager
import com.estatia.realestate.apps.core.security.keystore.AndroidKeyStoreManager
import com.estatia.realestate.apps.core.security.mappers.SecurityExceptionTranslator
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.plus

@Module
@InstallIn(SingletonComponent::class)
abstract class SecurityModule {

    @Binds
    @Singleton
    @Suppress("unused")
    abstract fun bindApiKeyProvider(impl: BuildConfigApiKeyProvider): ApiKeyProvider

    @Binds
    @Singleton
    @Suppress("unused")
    abstract fun bindTokenLocalDataSource(impl: TokenLocalDataSource): ITokenLocalDataSource

    @Binds
    @Singleton
    @Suppress("unused")
    abstract fun bindKeyStoreManager(impl: AndroidKeyStoreManager): IKeyStoreManager

    @Binds
    @Singleton
    @Suppress("unused")
    abstract fun bindSecurityExceptionTranslator(impl: SecurityExceptionTranslator): ISecurityExceptionTranslator

    @Binds
    @Singleton
    @Suppress("unused")
    abstract fun bindCryptoExecutor(impl: CryptoExecutor): ICryptoExecutor

    @Binds
    @Singleton
    @Suppress("unused")
    abstract fun bindHashManager(impl: HashManager): IHashManager

    @Binds
    @Singleton
    @Suppress("unused")
    abstract fun bindAesCrypto(impl: AesGcmCryptoEngine): IAesGcmCryptoEngine

    @Binds
    @Singleton
    @Suppress("unused")
    abstract fun bindSignatureManager(impl: SignatureManager): ISignatureManager

    @Binds
    @Singleton
    @Suppress("unused")
    abstract fun bindRsaCrypto(impl: RsaCryptoEngine): IRsaCryptoEngine

    companion object {
        @Provides
        @Singleton
        @Suppress("DEPRECATION")
        fun provideTokenDataStore(
            @ApplicationContext context: Context,
            @Dispatcher(EstatiaDispatchers.IO) ioDispatcher: CoroutineDispatcher,
            @ApplicationScope scope: CoroutineScope,
        ): DataStore<Preferences> =
            PreferenceDataStoreFactory.create(
                scope = scope + ioDispatcher,
                migrations = listOf(
                    SharedPreferencesMigration(
                        produceSharedPreferences = {
                            val masterKey = MasterKey.Builder(context)
                                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                                .build()
                            EncryptedSharedPreferences.create(
                                context,
                                "encrypted_prefs",
                                masterKey,
                                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
                            )
                        },
                    ),
                ),
            ) {
                context.preferencesDataStoreFile("secure_tokens")
            }
    }
}
