package com.estatia.realestate.apps.core.config.di

import com.estatia.realestate.apps.core.config.parser.ConfigParser
import com.estatia.realestate.apps.core.config.repository.ConfigRepositoryImpl
import com.estatia.realestate.apps.core.config.runtime.ConfigStateHolder
import com.estatia.realestate.apps.core.domain.interfaces.IConfigRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ConfigModule {

    @Binds
    @Singleton
    abstract fun bindConfigRepository(
        impl: ConfigRepositoryImpl
    ): IConfigRepository

    companion object {

        @Provides
        @Singleton
        fun provideParser() = ConfigParser()

        @Provides
        @Singleton
        fun provideStateHolder() = ConfigStateHolder()
    }
}
