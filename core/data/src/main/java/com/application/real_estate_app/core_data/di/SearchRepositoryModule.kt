package com.application.real_estate_app.core_data.di

import com.application.real_estate_app.core_data.interfaces.ISearchRepository
import com.application.real_estate_app.core_data.repositories.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindSearchRepo(repo: SearchRepository): ISearchRepository
}