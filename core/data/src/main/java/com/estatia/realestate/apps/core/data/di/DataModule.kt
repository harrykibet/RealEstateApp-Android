package com.estatia.realestate.apps.core.data.di

import com.estatia.realestate.apps.core.domain.interfaces.IAnalyticsTracker
import com.estatia.realestate.apps.core.domain.interfaces.IAuthRepository
import com.estatia.realestate.apps.core.domain.interfaces.ICommentsRepository
import com.estatia.realestate.apps.core.domain.interfaces.IConfigDataRepository
import com.estatia.realestate.apps.core.domain.interfaces.IExceptionTranslator
import com.estatia.realestate.apps.core.domain.interfaces.IPropertyRepository
import com.estatia.realestate.apps.core.domain.interfaces.ISearchRepository
import com.estatia.realestate.apps.core.domain.interfaces.ISecurityRepository
import com.estatia.realestate.apps.core.domain.interfaces.ISecretRepository
import com.estatia.realestate.apps.core.domain.interfaces.IUserRepository
import com.estatia.realestate.apps.core.data.mappers.exceptions.ExceptionTranslator
import com.estatia.realestate.apps.core.data.repositories.AnalyticsTracker
import com.estatia.realestate.apps.core.data.repositories.AuthRepository
import com.estatia.realestate.apps.core.data.repositories.CommentsRepository
import com.estatia.realestate.apps.core.data.repositories.ConfigDataRepository
import com.estatia.realestate.apps.core.data.repositories.PropertyRepository
import com.estatia.realestate.apps.core.data.repositories.SearchRepository
import com.estatia.realestate.apps.core.data.repositories.SecurityRepository
import com.estatia.realestate.apps.core.data.repositories.SecretRepository
import com.estatia.realestate.apps.core.data.repositories.UserRepository
import com.estatia.realestate.apps.core.data.util.TimeZoneBroadcastMonitor
import com.estatia.realestate.apps.core.data.util.TimeZoneMonitor
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindAnalyticsRepository(analyticsRepository: AnalyticsTracker) : IAnalyticsTracker

    @Binds
    @Singleton
    abstract fun bindAuthRepository(repository: AuthRepository) : IAuthRepository

    @Binds
    @Singleton
    abstract fun bindCommentsRepository(repository: CommentsRepository) : ICommentsRepository

    @Binds
    @Singleton
    abstract fun bindPropertyRepository(repository: PropertyRepository) : IPropertyRepository

    @Binds
    @Singleton
    abstract fun bindSearchRepo(repo: SearchRepository): ISearchRepository

    @Binds
    @Singleton
    abstract fun bindSecurityRepo(securityRepository: SecurityRepository) : ISecurityRepository

    @Binds
    @Singleton
    abstract fun bindSecretRepo(repository: SecretRepository) : ISecretRepository

    @Binds
    @Singleton
    abstract fun bindUserRepo(repo: UserRepository): IUserRepository

    @Binds
    @Singleton
    abstract fun bindConfigDataRepo(repo: ConfigDataRepository): IConfigDataRepository

    @Binds
    @Singleton
    abstract fun bindExceptionTranslator(translator: ExceptionTranslator): IExceptionTranslator

    @Binds
    internal abstract fun binds(impl: TimeZoneBroadcastMonitor): TimeZoneMonitor

    companion object {
        @Provides
        @Singleton
        fun provideGson(): Gson = Gson()
    }
}
