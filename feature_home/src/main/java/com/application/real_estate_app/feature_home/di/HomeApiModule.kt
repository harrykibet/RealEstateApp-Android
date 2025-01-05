package com.application.real_estate_app.feature_home.di

import com.application.real_estate_app.feature_home.data.apis.HomeApi
import com.application.real_estate_app.feature_home.domain.interfaces.IHomeApi
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeApiModule {
    @Binds
    @Singleton
    abstract fun bindHomeApi(api: HomeApi): IHomeApi
}