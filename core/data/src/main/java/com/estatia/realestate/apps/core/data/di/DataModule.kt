package com.estatia.realestate.apps.core.data.di

import com.estatia.realestate.apps.core.data.interfaces.IAnalyticsTracker
import com.estatia.realestate.apps.core.data.interfaces.IAuthRepository
import com.estatia.realestate.apps.core.data.interfaces.ICommentsRepository
import com.estatia.realestate.apps.core.data.interfaces.IExceptionTranslator
import com.estatia.realestate.apps.core.data.interfaces.IPropertyRepository
import com.estatia.realestate.apps.core.data.interfaces.ISearchRepository
import com.estatia.realestate.apps.core.data.interfaces.ISecurityRepository
import com.estatia.realestate.apps.core.data.interfaces.IUserRepository
import com.estatia.realestate.apps.core.data.mappers.exceptions.ExceptionTranslator
import com.estatia.realestate.apps.core.data.repositories.AnalyticsTracker
import com.estatia.realestate.apps.core.data.repositories.AuthRepository
import com.estatia.realestate.apps.core.data.repositories.CommentsRepository
import com.estatia.realestate.apps.core.data.repositories.PropertyRepository
import com.estatia.realestate.apps.core.data.repositories.SearchRepository
import com.estatia.realestate.apps.core.data.repositories.SecurityRepository
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
    abstract fun bindUserRepo(repo: UserRepository): IUserRepository

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
