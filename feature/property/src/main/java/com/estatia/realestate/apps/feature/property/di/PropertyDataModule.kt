package com.estatia.realestate.apps.feature.property.di

import com.estatia.realestate.apps.feature.property.utils.PropertyData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.estatia.realestate.apps.core.architecture.annotations.Helper

@Module
@InstallIn(SingletonComponent::class)
@Helper
object PropertyDataModule {

    @Singleton
    @Provides
    fun providePropertyData() = PropertyData()
}
