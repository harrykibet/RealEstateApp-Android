package com.application.real_estate_app.feature_comments.di

import com.application.real_estate_app.feature_comments.data.repositories.CommentsRepository
import com.application.real_estate_app.feature_comments.domain.interfaces.ICommentsRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CommentsRepoModule {
    @Binds
    @Singleton
    abstract fun bindCommentsRepo(repo: CommentsRepository): ICommentsRepo
}