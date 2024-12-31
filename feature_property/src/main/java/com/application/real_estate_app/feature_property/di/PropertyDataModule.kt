package com.application.real_estate_app.feature_property.di

import com.application.real_estate_app.feature_property.data.utils.PropertyData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PropertyDataModule {

    @Singleton
    @Provides
    fun providePropertyData() = PropertyData()
}
