package com.application.real_estate_app.feature_property.di

import com.application.real_estate_app.feature_property.data.apis.PropertyApi
import com.application.real_estate_app.feature_property.domain.interfaces.IPropertyApi
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PropertyApiModule {
    @Binds
    @Singleton
    abstract fun bindPropertyApi(api: PropertyApi) : IPropertyApi
}