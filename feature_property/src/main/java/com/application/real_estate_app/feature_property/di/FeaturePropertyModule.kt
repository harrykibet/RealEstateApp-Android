package com.application.real_estate_app.feature_property.di

import com.application.real_estate_app.feature_property.data.repositories.FeaturePropertyRepo
import com.application.real_estate_app.feature_property.domain.interfaces.IFeaturePropertyRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeaturePropertyModule {
    @Binds
    @Singleton
    abstract fun bindFeaturePropertyRepo(repository: FeaturePropertyRepo) : IFeaturePropertyRepo
}