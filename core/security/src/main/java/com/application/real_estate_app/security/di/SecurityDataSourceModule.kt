package com.application.real_estate_app.security.di

import com.application.real_estate_app.security.data.sources.local.SecurityDataSource
import com.application.real_estate_app.security.domain.interfaces.ISecurityDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SecurityDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindSecurityDataSource(securityDataSource: SecurityDataSource) : ISecurityDataSource
}