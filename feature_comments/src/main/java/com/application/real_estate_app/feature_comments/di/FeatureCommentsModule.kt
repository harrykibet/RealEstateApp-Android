package com.application.real_estate_app.feature_comments.di

import com.application.real_estate_app.feature_comments.data.repositories.FeatureCommentsRepo
import com.application.real_estate_app.feature_comments.domain.interfaces.IFeatureCommentsRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureCommentsModule {
    @Binds
    @Singleton
    abstract fun bindFeatureCommentsRepo(repository: FeatureCommentsRepo): IFeatureCommentsRepo
}