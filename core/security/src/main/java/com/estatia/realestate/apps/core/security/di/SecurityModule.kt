package com.estatia.realestate.apps.core.security.di

import android.content.SharedPreferences
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
import com.estatia.realestate.apps.core.security.keystore.AndroidKeyStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideApiKeyProvider(): ApiKeyProvider =
        BuildConfigApiKeyProvider()

    @Provides
    @Singleton
    fun provideTokenLocalDataSource(sharedPreferences: SharedPreferences): ITokenLocalDataSource =
        TokenLocalDataSource(sharedPreferences)


    @Provides
    @Singleton
    fun provideKeyStoreManager(): IKeyStoreManager =
        AndroidKeyStoreManager()

    @Provides
    @Singleton
    fun provideAesCrypto(
        keystoreManager: IKeyStoreManager
    ): IAesGcmCryptoEngine = AesGcmCryptoEngine(keystoreManager)

    @Provides
    @Singleton
    fun provideSignatureManager(
        keystoreManager: IKeyStoreManager
    ): ISignatureManager = SignatureManager(keystoreManager)


    @Provides
    @Singleton
    fun provideRsaCrypto(
        keystoreManager: IKeyStoreManager
    ): IRsaCryptoEngine = RsaCryptoEngine(keystoreManager)
}