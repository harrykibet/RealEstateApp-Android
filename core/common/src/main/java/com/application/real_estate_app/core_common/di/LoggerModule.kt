package com.application.real_estate_app.core_common.di

import com.application.real_estate_app.core_common.logs.Logger
import com.application.real_estate_app.core_interface.LoggerInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LoggerModule {
    @Binds
    @Singleton
    abstract fun bindLogger(logger: Logger): LoggerInterface
}