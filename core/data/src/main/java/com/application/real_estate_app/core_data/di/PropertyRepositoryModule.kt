package com.application.real_estate_app.core_data.di

import com.application.real_estate_app.core_data.repositories.PropertyRepository
import com.application.real_estate_app.core_interface.IPropertyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class PropertyRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPropertyRepository(repository: PropertyRepository) : IPropertyRepository
}