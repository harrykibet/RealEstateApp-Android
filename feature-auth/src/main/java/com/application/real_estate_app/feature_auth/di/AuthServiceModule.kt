package com.application.real_estate_app.feature_auth.di

import com.application.real_estate_app.feature_auth.interfaces.AuthService
import com.application.real_estate_app.feature_auth.services.AuthServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthServiceModule {
    // Bind the AuthServiceImpl to the AuthService interface
    @Binds
    @Singleton
    abstract fun bindAuthService(authService: AuthServiceImpl): AuthService
}