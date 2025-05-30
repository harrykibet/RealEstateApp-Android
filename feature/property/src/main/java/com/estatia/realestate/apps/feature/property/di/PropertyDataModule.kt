package com.estatia.realestate.apps.feature.property.di

import com.estatia.realestate.apps.feature.property.utils.PropertyData
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
