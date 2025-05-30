package com.estatia.realestate.apps.core.security.di

import com.estatia.realestate.apps.core.security.interfaces.ILocalCryptoManager
import com.estatia.realestate.apps.core.security.LocalCryptoManager
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