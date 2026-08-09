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

    companion object {
        @Provides
        @Singleton
        fun provideGson(): Gson = Gson()
    }
}
