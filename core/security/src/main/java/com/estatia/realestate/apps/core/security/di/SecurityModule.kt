package com.estatia.realestate.apps.core.security.di

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
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

@Module
@InstallIn(SingletonComponent::class)
abstract class SecurityModule {

    @Binds
    @Singleton
    abstract fun bindApiKeyProvider(impl: BuildConfigApiKeyProvider): ApiKeyProvider

    @Binds
    @Singleton
    abstract fun bindTokenLocalDataSource(impl: TokenLocalDataSource): ITokenLocalDataSource

    @Binds
    @Singleton
    abstract fun bindKeyStoreManager(impl: AndroidKeyStoreManager): IKeyStoreManager

    @Binds
    @Singleton
    abstract fun bindSecurityExceptionTranslator(impl: SecurityExceptionTranslator): ISecurityExceptionTranslator

    @Binds
    @Singleton
    abstract fun bindCryptoExecutor(impl: CryptoExecutor): ICryptoExecutor

    @Binds
    @Singleton
    abstract fun bindHashManager(impl: HashManager): IHashManager

    @Binds
    @Singleton
    abstract fun bindAesCrypto(impl: AesGcmCryptoEngine): IAesGcmCryptoEngine

    @Binds
    @Singleton
    abstract fun bindSignatureManager(impl: SignatureManager): ISignatureManager

    @Binds
    @Singleton
    abstract fun bindRsaCrypto(impl: RsaCryptoEngine): IRsaCryptoEngine

    companion object {
        @Provides
        @Singleton
        fun provideEncryptedSharedPreferences(
            @ApplicationContext context: Context
        ): SharedPreferences {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            return EncryptedSharedPreferences.create(
                context,
                "encrypted_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }
}
