package com.estatia.realestate.apps.core.data.di

import com.estatia.realestate.apps.core.data.mappers.exceptions.ExceptionTranslator
import com.estatia.realestate.apps.core.data.repositories.*
import com.estatia.realestate.apps.core.domain.interfaces.*
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
    internal abstract fun bindAnalyticsRepository(analyticsRepository: AnalyticsTracker) : IAnalyticsTracker

    @Binds
    @Singleton
    internal abstract fun bindAuthRepository(repository: AuthRepository) : IAuthRepository

    @Binds
    @Singleton
    internal abstract fun bindCommentsRepository(repository: CommentsRepository) : ICommentsRepository

    @Binds
    @Singleton
    internal abstract fun bindPropertyRepository(repository: PropertyRepository) : IPropertyRepository

    @Binds
    @Singleton
    internal abstract fun bindSearchRepo(repo: SearchRepository): ISearchRepository

    @Binds
    @Singleton
    internal abstract fun bindSecurityRepo(securityRepository: SecurityRepository) : ISecurityRepository

    @Binds
    @Singleton
    internal abstract fun bindSecretRepo(repository: SecretRepository) : ISecretRepository

    @Binds
    @Singleton
    internal abstract fun bindUserRepo(repo: UserRepository): IUserRepository

    @Binds
    @Singleton
    internal abstract fun bindConfigDataRepo(repo: ConfigDataRepository): IConfigDataRepository

    @Binds
    @Singleton
    internal abstract fun bindEngagementRepo(repo: EngagementRepository): IEngagementRepository

    @Binds
    @Singleton
    internal abstract fun bindExceptionTranslator(translator: ExceptionTranslator): IExceptionTranslator

    companion object {
        @Provides
        @Singleton
        fun provideGson(): Gson = Gson()
    }
}
