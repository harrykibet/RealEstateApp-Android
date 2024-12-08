package com.application.real_estate_app.feature_auth.di

import com.application.real_estate_app.domain.interfaces.IUserRepository
import com.application.real_estate_app.feature_auth.repositories.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserRepositoryModule {
    //Bind the IUserRepository to UserRepositoryImpl
    @Binds
    @Singleton
    abstract fun bindIUserRepository(userRepositoryImpl: UserRepositoryImpl): IUserRepository
}