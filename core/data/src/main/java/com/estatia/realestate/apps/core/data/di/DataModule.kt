package com.estatia.realestate.apps.core.data.di

import com.estatia.realestate.apps.core.data.mappers.exceptions.ExceptionTranslator
import com.estatia.realestate.apps.core.data.repositories.AnalyticsTracker
import com.estatia.realestate.apps.core.data.repositories.AuthRepository
import com.estatia.realestate.apps.core.data.repositories.CommentsRepository
import com.estatia.realestate.apps.core.data.repositories.ConfigDataRepository
import com.estatia.realestate.apps.core.data.repositories.EngagementRepository
import com.estatia.realestate.apps.core.data.repositories.PaymentsRepository
import com.estatia.realestate.apps.core.data.repositories.PropertyRepository
import com.estatia.realestate.apps.core.data.repositories.SearchRepository
import com.estatia.realestate.apps.core.data.repositories.SecretRepository
import com.estatia.realestate.apps.core.data.repositories.SecurityRepository
import com.estatia.realestate.apps.core.data.repositories.UserRepository
import com.estatia.realestate.apps.core.domain.analytics.IAnalyticsTracker
import com.estatia.realestate.apps.core.domain.analytics.IEngagementRepository
import com.estatia.realestate.apps.core.domain.common.IExceptionTranslator
import com.estatia.realestate.apps.core.domain.config.IConfigDataRepository
import com.estatia.realestate.apps.core.domain.repository.ICommentsRepository
import com.estatia.realestate.apps.core.domain.repository.IPropertyRepository
import com.estatia.realestate.apps.core.domain.repository.ISearchRepository
import com.estatia.realestate.apps.core.domain.repository.IUserRepository
import com.estatia.realestate.apps.core.domain.repository.IPaymentsRepository
import com.estatia.realestate.apps.core.domain.security.IAuthRepository
import com.estatia.realestate.apps.core.domain.security.ISecretRepository
import com.estatia.realestate.apps.core.domain.security.ISecurityRepository
import kotlinx.serialization.json.Json
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
    internal abstract fun bindPaymentsRepo(repo: PaymentsRepository): IPaymentsRepository

    @Binds
    @Singleton
    internal abstract fun bindExceptionTranslator(translator: ExceptionTranslator): IExceptionTranslator

    companion object {
        @Provides
        @Singleton
        fun provideJson(): Json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            encodeDefaults = true
        }
    }
}
