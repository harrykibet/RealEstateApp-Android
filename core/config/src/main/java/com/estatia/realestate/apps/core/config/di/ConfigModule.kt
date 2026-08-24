package com.estatia.realestate.apps.core.config.di

import com.estatia.realestate.apps.core.config.parser.ConfigParser
import com.estatia.realestate.apps.core.config.provider.ConfigProvider
import com.estatia.realestate.apps.core.config.runtime.ConfigStateHolder
import com.estatia.realestate.apps.core.domain.config.IConfigLifecycle
import com.estatia.realestate.apps.core.domain.config.IConfigProvider
import com.estatia.realestate.apps.core.domain.config.INetworkConfig
import com.estatia.realestate.apps.core.domain.config.IPlayerTuningConfig
import com.estatia.realestate.apps.core.domain.config.ISecurityConfig
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
    internal abstract fun bindConfigRepository(
        impl: ConfigProvider
    ): IConfigProvider

    @Binds
    @Singleton
    internal abstract fun bindConfigLifecycle(
        impl: ConfigProvider
    ): IConfigLifecycle

    @Binds
    @Singleton
    internal abstract fun bindNetworkConfig(
        impl: ConfigProvider
    ): INetworkConfig

    @Binds
    @Singleton
    internal abstract fun bindSecurityConfig(
        impl: ConfigProvider
    ): ISecurityConfig

    @Binds
    @Singleton
    internal abstract fun bindPlayerTuningConfig(
        impl: ConfigProvider
    ): IPlayerTuningConfig

    companion object {

        @Provides
        @Singleton
        fun provideParser() = ConfigParser()

        @Provides
        @Singleton
        fun provideStateHolder() = ConfigStateHolder()
    }
}
