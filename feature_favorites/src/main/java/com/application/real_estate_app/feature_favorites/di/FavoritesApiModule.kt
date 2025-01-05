package com.application.real_estate_app.feature_favorites.di

import com.application.real_estate_app.feature_favorites.data.apis.FavoritesApi
import com.application.real_estate_app.feature_favorites.domain.interfaces.IFavoritesApi
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FavoritesApiModule {
    @Binds
    @Singleton
    abstract fun bindFavoritesApi(api: FavoritesApi): IFavoritesApi
}