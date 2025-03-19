package com.application.real_estate_app.feature_search.di

import com.application.real_estate_app.feature_search.data.repositories.SearchRepository
import com.application.real_estate_app.feature_search.domain.interfaces.ISearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchRepoModule {
    @Binds
    @Singleton
    abstract fun bindSearchRepo(repo: SearchRepository): ISearchRepository
}