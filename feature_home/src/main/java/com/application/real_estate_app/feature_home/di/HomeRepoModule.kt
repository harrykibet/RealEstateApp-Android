package com.application.real_estate_app.feature_home.di

import com.application.real_estate_app.feature_home.data.repositories.HomeRepository
import com.application.real_estate_app.feature_home.domain.interfaces.IHomeRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeRepoModule {
    @Binds
    @Singleton
    abstract fun bindHomeRepo(repo: HomeRepository): IHomeRepo
}