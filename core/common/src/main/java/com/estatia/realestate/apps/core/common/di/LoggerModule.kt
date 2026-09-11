package com.estatia.realestate.apps.core.common.di

import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.common.logs.Logger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.estatia.realestate.apps.core.architecture.annotations.Logic.Utility

@Module
@InstallIn(SingletonComponent::class)
@Utility
abstract class LoggerModule {
    @Binds
    @Singleton
    abstract fun bindLogger(logger: Logger): ILogger
}
