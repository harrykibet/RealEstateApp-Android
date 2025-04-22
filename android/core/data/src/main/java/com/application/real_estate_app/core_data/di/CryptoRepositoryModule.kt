package com.application.real_estate_app.core_data.di

import com.application.real_estate_app.core_data.interfaces.ICryptoRepository
import com.application.real_estate_app.core_data.repositories.CryptoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CryptoRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCryptoRepository(cryptoRepository: CryptoRepository) : ICryptoRepository
}