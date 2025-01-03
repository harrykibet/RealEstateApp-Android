package com.application.real_estate_app.feature_favorites.di

import com.application.real_estate_app.feature_favorites.data.repositories.FeatureFavoritesRepo
import com.application.real_estate_app.feature_favorites.domain.interfaces.IFeatureFavoritesRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureFavoritesModule {
    @Binds
    @Singleton
    abstract fun bindFeatureFavoritesRepo(repository: FeatureFavoritesRepo): IFeatureFavoritesRepo
}