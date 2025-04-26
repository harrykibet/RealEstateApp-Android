package com.application.real_estate_app.core_data.di

import com.application.real_estate_app.core_data.interfaces.ICommentsRepository
import com.application.real_estate_app.core_data.repositories.CommentsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CommentsRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCommentsRepository(repository: CommentsRepository) : ICommentsRepository
}