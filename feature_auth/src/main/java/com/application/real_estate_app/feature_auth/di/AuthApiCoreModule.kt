package com.application.real_estate_app.feature_auth.di


import com.application.real_estate_app.core.interfaces.AuthApiInterface
import com.application.real_estate_app.feature_auth.data.services.AuthApiCore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthApiCoreModule {

    // Binds the Core module interface IAuthApi_Core  to its implementation
    @Binds
    @Singleton
    abstract fun bindAuthApiCore(authApi: AuthApiCore): AuthApiInterface
}
