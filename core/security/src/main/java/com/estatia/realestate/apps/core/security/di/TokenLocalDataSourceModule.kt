package com.estatia.realestate.apps.core.security.di

import com.estatia.realestate.apps.core.security.TokenLocalDataSource
import com.estatia.realestate.apps.core.security.interfaces.ITokenLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class TokenLocalDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindTokenLocalDataSource(tokenLocalDataSource: TokenLocalDataSource) : ITokenLocalDataSource
}