package com.application.real_estate_app.feature_comments.di

import com.application.real_estate_app.feature_comments.data.apis.CommentsApi
import com.application.real_estate_app.feature_comments.domain.interfaces.ICommentsApi
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CommentsApiModule {
    @Binds
    @Singleton
    abstract fun bindCommentsApi(api: CommentsApi): ICommentsApi
}