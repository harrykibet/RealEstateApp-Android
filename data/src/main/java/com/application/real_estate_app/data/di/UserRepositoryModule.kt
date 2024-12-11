package com.application.real_estate_app.data.di

import com.application.real_estate_app.data.repositories.UserRepositoryImpl
import com.application.real_estate_app.domain.interfaces.IUserRepository
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