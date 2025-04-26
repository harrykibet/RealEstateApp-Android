package com.application.real_estate_app.security.di

import com.application.real_estate_app.security.TokenLocalDataSource
import com.application.real_estate_app.security.interfaces.ITokenLocalDataSource
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