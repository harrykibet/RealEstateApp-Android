package com.application.real_estate_app.security.di

import com.application.real_estate_app.security.interfaces.ILocalCryptoManager
import com.application.real_estate_app.security.LocalCryptoManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalCryptoManagerModule {
    @Binds
    @Singleton
    abstract fun bindCryptoManager(cryptoManager: LocalCryptoManager) : ILocalCryptoManager
}