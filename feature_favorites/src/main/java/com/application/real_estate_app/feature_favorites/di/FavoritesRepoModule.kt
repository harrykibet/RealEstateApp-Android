package com.application.real_estate_app.feature_favorites.di

import com.application.real_estate_app.feature_favorites.data.repositories.FavoritesRepository
import com.application.real_estate_app.feature_favorites.domain.interfaces.IFavoritesRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FavoritesRepoModule {
    @Binds
    @Singleton
    abstract fun bindFavoritesRepo(repo: FavoritesRepository): IFavoritesRepo
}