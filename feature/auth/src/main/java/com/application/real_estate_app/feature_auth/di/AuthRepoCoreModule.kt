package com.application.real_estate_app.feature_auth.di


import com.application.real_estate_app.core_interface.AuthRepoInterface
import com.application.real_estate_app.feature_auth.data.services.ImplAuthRepoCore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepoCoreModule {

    // Binds the Core module interface IAuthApi_Core  to its implementation
    @Binds
    @Singleton
    abstract fun bindAuthRepoCore(authRepo: ImplAuthRepoCore): AuthRepoInterface
}
