package com.application.real_estate_app.core_data.di

import com.application.real_estate_app.core_data.interfaces.IAuthRepository
import com.application.real_estate_app.core_data.repositories.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(repository: AuthRepository) : IAuthRepository
}