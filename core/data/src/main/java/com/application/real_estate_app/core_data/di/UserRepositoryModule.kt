package com.application.real_estate_app.core_data.di

import com.application.real_estate_app.core_data.interfaces.IUserRepository
import com.application.real_estate_app.core_data.repositories.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUserRepo(repo: UserRepository): IUserRepository
}