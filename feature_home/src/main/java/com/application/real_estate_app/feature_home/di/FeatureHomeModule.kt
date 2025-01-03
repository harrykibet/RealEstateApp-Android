package com.application.real_estate_app.feature_home.di

import com.application.real_estate_app.feature_home.data.repositories.FeatureHomeRepo
import com.application.real_estate_app.feature_home.domain.interfaces.IFeatureHomeRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureHomeModule {
    @Binds
    @Singleton
    abstract fun bindFeatureHomeRepo(repository: FeatureHomeRepo): IFeatureHomeRepo
}