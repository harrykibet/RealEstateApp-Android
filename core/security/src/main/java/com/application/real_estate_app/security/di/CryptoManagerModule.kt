package com.application.real_estate_app.security.di

import com.application.real_estate_app.security.domain.interfaces.ICryptoManager
import com.application.real_estate_app.security.data.sources.local.CryptoManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CryptoManagerModule {
    @Binds
    @Singleton
    abstract fun bindCryptoManager(cryptoManager: CryptoManager) : ICryptoManager
}